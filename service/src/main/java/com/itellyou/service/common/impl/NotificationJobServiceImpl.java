package com.itellyou.service.common.impl;

import com.itellyou.model.common.NotificationDisplay;
import com.itellyou.model.common.NotificationDisplayModel;
import com.itellyou.model.common.NotificationMarkModel;
import com.itellyou.model.common.NotificationQueueModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.common.*;
import com.itellyou.util.DateUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.*;

public class NotificationJobServiceImpl extends QuartzJobBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final NotificationQueueService queueService;
    private final NotificationMarkService markService;
    private final NotificationDisplayService displayService;
    private final NotificationService notificationService;
    private final NotificationManagerService managerService;

    public NotificationJobServiceImpl(NotificationQueueService queueService, NotificationMarkService markService, NotificationDisplayService displayService, NotificationService notificationService, NotificationManagerService managerService) {
        this.queueService = queueService;
        this.markService = markService;
        this.displayService = displayService;
        this.notificationService = notificationService;
        this.managerService = managerService;
    }

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) {
        try {
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            Long userId = dataMap.getLong("userId");
            synchronized (userId) {
                // 获取用户上一次读取消息的时间标记，如果没有标记就获取全部的一对多和一对一消息队列
                NotificationMarkModel markModel = markService.findByUserId(userId);
                LocalDateTime currentTime = DateUtils.toLocalDateTime();
                Long beginTime = markModel == null ? null : DateUtils.getTimestamp(markModel.getUpdatedTime());
                // 用户设置需要接收消息的操作Map
                Map<EntityAction, Collection<EntityType>> actionsMap = new LinkedHashMap<>();
                // 是否接收 关注的提问有新回答
                boolean displayPublishAnswer = false;
                // 是否接收 关注的专栏是否有新文章
                boolean displayPublishArticle = false;
                // 获取用户默认消息接收设置
                List<NotificationDisplayModel> displayModels = displayService.searchByDefault(userId, null, null);
                // 根据用户消息设置，设置 actionsMap
                for (NotificationDisplayModel displayModel : displayModels) {
                    EntityAction action = displayModel.getAction();
                    EntityType type = displayModel.getType();
                    // 有新的发布操作，判断用户是否有设置，[专栏是否有新文章,关注的提问有新回答]这样的一对多消息
                    if (action.equals(EntityAction.PUBLISH)) {
                        if (type.equals(EntityType.ANSWER)) {
                            displayPublishAnswer = !displayModel.getValue().equals(NotificationDisplay.NONE);
                        }
                        if (type.equals(EntityType.ARTICLE)) {
                            displayPublishArticle = !displayModel.getValue().equals(NotificationDisplay.NONE);
                        }
                    } else if (!displayModel.getValue().equals(NotificationDisplay.NONE)) {// 获取其它一对一的消息设置
                        if (actionsMap.containsKey(action)) {
                            actionsMap.get(action).add(type);
                        } else {
                            actionsMap.put(action, new LinkedHashSet<EntityType>() {{
                                add(type);
                            }});
                        }
                    }
                }
                // 时间倒序获取消息队列
                Map<String, String> order = new HashMap<>();
                order.put("created_time", "desc");

                List<NotificationQueueModel> queueModels = new ArrayList<>();
                // 如果用户消息设置有接收 关注的问题有新的回答 一对多消息队列
                if (displayPublishAnswer) {
                    // 查询 关注的问题有新的回答 一对多消息队列
                    queueModels.addAll(queueService.searchPublishAnswer(userId, beginTime, null, null, order, null, null));
                }
                // 如果用户消息设置有接收 关注的专栏有新的文章 一对多消息队列
                if (displayPublishArticle) {
                    // 查询 关注的专栏有新的文章 一对多消息队列
                    queueModels.addAll(queueService.searchPublishArticle(userId, beginTime, null, null, order, null, null));
                }
                // 如果用户消息设置有接收的消息
                if (actionsMap.size() > 0) {
                    // 查询 一对一 消息队列
                    queueModels.addAll(queueService.search(null, actionsMap, userId, null, beginTime, null, null, order, null, null));
                }
                // 如果没有要读取的队列消息，直接读取是否有消息返回
                if (queueModels.size() == 0) {
                    managerService.sendEvent(userId);
                    return;
                };

                try {
                    // 整合消息队列，合并相关消息为一条记录，并写入消息表
                    int result = notificationService.insert(userId, queueModels.toArray(new NotificationQueueModel[queueModels.size()]));
                    if (result < 1) throw new Exception("写入消息通知错误");

                    // 标记当前用户的读取时间
                    result = markService.insertOrUpdate(new NotificationMarkModel(userId, currentTime));
                    if (result < 1) throw new Exception("记录mark失败");

                    // 发布消息事件
                    managerService.sendEvent(userId);

                } catch (Exception e) {
                    logger.error(e.getLocalizedMessage());
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }
}
