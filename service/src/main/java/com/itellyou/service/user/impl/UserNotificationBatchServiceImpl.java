package com.itellyou.service.user.impl;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.*;
import com.itellyou.service.user.*;
import com.itellyou.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.function.BiConsumer;

@Service
public class UserNotificationBatchServiceImpl implements UserNotificationBatchService {

    private final UserNotificationService notificationService;
    private final UserNotificationMarkService markService;
    private final UserNotificationQueueService queueService;
    private final UserNotificationDisplayService displayService;
    private final UserOperationalService operationalService;

    @Autowired
    public UserNotificationBatchServiceImpl(UserNotificationService notificationService, UserNotificationQueueService queueService, UserNotificationMarkService markService,
                                            UserNotificationDisplayService displayService,UserOperationalService operationalService){
        this.notificationService = notificationService;
        this.queueService = queueService;
        this.markService = markService;
        this.displayService = displayService;
        this.operationalService = operationalService;
    }

    private Map<Long,Boolean> aliveMap = new HashMap<>();
    private Map<Long,Thread> threadMap = new HashMap<>();

    private List<UserNotificationGroupCountModel> getGroupCount(Long userId){
        return notificationService.groupCount(null,false,false,userId,null,null,null);
    }

    private void sendCount(Long userId,BiConsumer callback){
        List<UserNotificationGroupCountModel> groupCountModels = getGroupCount(userId);
        callback.accept(userId,groupCountModels);
    }

    @Override
    @Async
    public void start(Long userId, BiConsumer callback) {
        if(!aliveMap.containsKey(userId) || !aliveMap.get(userId)){
            try {
                aliveMap.put(userId,true);
                threadMap.put(userId,Thread.currentThread());
                while (aliveMap.containsKey(userId) && aliveMap.get(userId)) {
                    this.doStart(userId);
                    sendCount(userId,callback);
                    Thread.sleep(300000);
                }
                threadMap.remove(userId);
                aliveMap.remove(userId);
            }catch (Exception e){
                //e.printStackTrace();
            }
        }else{
            sendCount(userId,callback);
        }
    }

    @Override
    public void stop(Long userId) {
        if(aliveMap.containsKey(userId)){
            aliveMap.remove(userId);
        }
        if(threadMap.containsKey(userId)){
            Thread thread = threadMap.get(userId);
            if(thread.isAlive()){
                thread.interrupt();
                threadMap.remove(userId);
            }
        }
    }

    @Override
    @Async
    @Transactional
    public void doStart(Long userId) {
        UserNotificationMarkModel markModel = markService.findByUserId(userId);
        Long beginTime = markModel == null ? null : markModel.getUpdatedTime();
        Map<UserOperationalAction, HashSet<EntityType>> actionsMap = new LinkedHashMap<>();
        boolean displayPublishAnswer = false;
        boolean displayPublishArticle = false;
        List<UserNotificationDisplayModel> displayModels = displayService.searchByDefault(userId,null,null);
        for (UserNotificationDisplayModel displayModel:displayModels) {
            UserOperationalAction action = displayModel.getAction();
            EntityType type = displayModel.getType();
            if(action.equals(UserOperationalAction.PUBLISH) && type.equals(EntityType.ANSWER)){
                displayPublishAnswer = !displayModel.getValue().equals(UserNotificationDisplay.NONE);
            }else if(action.equals(UserOperationalAction.PUBLISH) && type.equals(EntityType.ARTICLE)){
                displayPublishArticle = !displayModel.getValue().equals(UserNotificationDisplay.NONE);
            }else if(!displayModel.getValue().equals(UserNotificationDisplay.NONE)){
                if(actionsMap.containsKey(action)){
                    actionsMap.get(action).add(type);
                }else{
                    actionsMap.put(action,new LinkedHashSet<EntityType>(){{ add(type);}});
                }
            }
        }
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");

        List<UserOperationalModel> queueModels = new ArrayList<>();
        if(displayPublishAnswer){
            queueModels.addAll(queueService.searchPublishAnswer(userId,beginTime,null,null,order,null,null));
        }
        if(displayPublishArticle){
            queueModels.addAll(queueService.searchPublishArticle(userId,beginTime,null,null,order,null,null));
        }
        if(actionsMap.size() > 0){
            queueModels.addAll(operationalService.search(null,actionsMap,userId,null,false,beginTime,null,null,order,null,null));
        }
        if(queueModels.size() == 0) return;
        try {
            int result = notificationService.insert(userId,queueModels.toArray(new UserOperationalModel[queueModels.size()]));
            if(result < 1) throw new Exception("写入消息通知错误");
            markService.insertOrUpdate(new UserNotificationMarkModel(userId, DateUtils.getTimestamp()));
            if(result < 1) throw new Exception("记录mark失败");
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
}
