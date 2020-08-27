package com.itellyou.service.common.impl;

import com.itellyou.model.common.NotificationDisplay;
import com.itellyou.model.common.NotificationDisplayModel;
import com.itellyou.model.common.NotificationQueueModel;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.event.NotificationEvent;
import com.itellyou.service.common.NotificationDisplayService;
import com.itellyou.service.common.NotificationManagerService;
import com.itellyou.service.common.NotificationQueueService;
import com.itellyou.service.common.NotificationService;
import com.itellyou.service.event.NotificationPublisher;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationManagerServiceImpl implements NotificationManagerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final NotificationQueueService queueService;
    private final NotificationDisplayService displayService;
    private final Scheduler scheduler;
    private final NotificationService notificationService;
    private final NotificationPublisher publisherEvent;

    public NotificationManagerServiceImpl(NotificationQueueService queueService, NotificationDisplayService displayService, Scheduler scheduler, NotificationService notificationService, NotificationPublisher publisherEvent) {
        this.queueService = queueService;
        this.displayService = displayService;
        this.scheduler = scheduler;
        this.notificationService = notificationService;
        this.publisherEvent = publisherEvent;
    }

    public String getKey(Long userId){
        return new StringBuilder("notificationManager-").append(userId).toString();
    }

    public TriggerKey getTriggerKey(Long userId){
        return new TriggerKey(getKey(userId));
    }

    public JobKey getJobKey(Long userId){
        return new JobKey(getKey(userId));
    }

    @Override
    public void addJob(Long userId){
        String id = getKey(userId);
        JobDetail job = JobBuilder.newJob(NotificationJobServiceImpl.class).withIdentity(id).usingJobData("userId",userId).storeDurably().build();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/5 * * * ?");

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(id)
                .withSchedule(cronScheduleBuilder)
                .build();

        try {
            scheduler.scheduleJob(job, trigger);
            scheduler.triggerJob(getJobKey(userId));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void removeJob(Long userId){
        try {
            TriggerKey triggerKey = getTriggerKey(userId);
            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(getJobKey(userId));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    @Async
    public void put(OperationalModel model) {
        try{
            if(model == null) return;
            // 获取系统默认消息设置
            List<NotificationDisplayModel> displayModels = displayService.getDefault();
            // 匹配哪些操作是需要设置消息的
            for (NotificationDisplayModel displayModel : displayModels){
                if(displayModel.getAction().equals(model.getAction()) && displayModel.getType().equals(model.getType()) && !displayModel.getValue().equals(NotificationDisplay.NONE)){
                    // 一个用户 收藏/关注/点赞 撤销后，再次触发操作，只需要更新之前消息队列的创建时间，否则会出现相同消息多次
                    switch (model.getAction()) {
                        case FOLLOW:
                        case LIKE:
                            List<NotificationQueueModel> queueModelList = queueService.find(model.getAction(),model.getType(),model.getCreatedUserId(),model.getTargetId());
                            if(queueModelList.size() > 0){
                                NotificationQueueModel updateQueueModel = queueModelList.get(0);
                                // 在30分钟内撤销后重复点击视为无效，不加入消息队列，不更新消息队列时间
                                if(model.getCreatedTime() - updateQueueModel.getCreatedTime() > 30 * 60){
                                    queueService.update(updateQueueModel.getId(),model.getCreatedTime(),model.getCreatedIp());
                                }else{
                                    logger.warn("Abandoned notification action : {},type:{}",model.getAction(),model.getType());
                                }
                                return;
                            }
                    }
                    NotificationQueueModel queueModel = new NotificationQueueModel(model.getAction(),model.getType(),model.getTargetId(),model.getTargetUserId(),model.getCreatedUserId(),model.getCreatedTime(),model.getCreatedIp());
                    queueService.insert(queueModel);
                    return;
                }
            }
            //logger.warn("Put notification not match action : {},type:{}",model.getAction(),model.getType());
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void sendEvent(Long userId) {
        // 发布消息事件
        NotificationEvent notificationEvent = new NotificationEvent(this,userId,
                notificationService.groupCount(null,false,false,userId,null,null,null));
        publisherEvent.publish(notificationEvent);
    }
}
