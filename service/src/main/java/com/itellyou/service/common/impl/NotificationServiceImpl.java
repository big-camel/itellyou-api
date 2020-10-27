package com.itellyou.service.common.impl;

import com.itellyou.dao.common.NotificationDao;
import com.itellyou.model.common.*;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.PageModel;
import com.itellyou.service.common.NotificationActorsService;
import com.itellyou.service.common.NotificationService;
import com.itellyou.service.common.OperationalService;
import com.itellyou.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDao notificationDao;
    private final NotificationActorsService actorsService;
    private final OperationalService operationalService;

    @Autowired
    public NotificationServiceImpl(NotificationDao notificationDao, NotificationActorsService actorsService, OperationalService operationalService){
        this.notificationDao = notificationDao;
        this.actorsService = actorsService;
        this.operationalService = operationalService;
    }

    @Override
    @Transactional
    public int insert(NotificationModel model, NotificationActorsModel... actors) throws Exception {
        try{
            int result = notificationDao.insert(model);
            if(result != 1) throw new Exception("写入通知失败");
            for (NotificationActorsModel actorsModel:actors) {
                Long notificationId = actorsModel.getNotificationId();
                if(notificationId == null || notificationId.equals(0l)){
                    actorsModel.setNotificationId(model.getId());
                }
            }
            result = actorsService.insert(actors);
            if(result != actors.length) throw new Exception("写入失败");
            return 1;
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    public int insert(NotificationModel... models) {
        return notificationDao.insert(models);
    }

    @Override
    @Transactional
    public int insert(Long receiveId, NotificationQueueModel... queueModels) throws Exception {
        Map<String, NotificationModel> notificationModelMap = new HashMap<>();
        Map<String,List<NotificationActorsModel>> actorsModelMap = new HashMap<>();
        for (NotificationQueueModel queueModel:queueModels) {
            String key = queueModel.getAction().toString() + "_" + queueModel.getType() + "_" + queueModel.getTargetId();
            NotificationActorsModel actorsModel = new NotificationActorsModel(null,queueModel.getCreatedUserId(),queueModel.getTargetId());
            if(notificationModelMap.containsKey(key)){
                if(actorsModelMap.get(key).contains(actorsModel)){
                    continue;
                }
                actorsModelMap.get(key).add(actorsModel);
                NotificationModel notificationModel = notificationModelMap.get(key);
                notificationModel.setMergeCount(notificationModel.getMergeCount() + 1);
            }else{
                List<NotificationActorsModel> actorsModels = new ArrayList<>();
                actorsModels.add(actorsModel);
                actorsModelMap.put(key,actorsModels);

                notificationModelMap.put(key,new NotificationModel(false,false,receiveId,queueModel.getAction(),queueModel.getType(),queueModel.getTargetId(),1, DateUtils.toLocalDateTime(),queueModel.getCreatedIp()));
            }
        }
        NotificationModel[] notificationModels = new NotificationModel[notificationModelMap.values().size()];
        int result = this.insert(notificationModelMap.values().toArray(notificationModels));
        if(result != notificationModels.length) throw new RuntimeException("写入通知失败");
        int keyIndex = 0;
        List<NotificationActorsModel> actorsList = new ArrayList<>();
        for (String key:notificationModelMap.keySet()) {
            for (NotificationActorsModel actorModel:actorsModelMap.get(key)) {
                actorModel.setNotificationId(notificationModels[keyIndex].getId());
                actorsList.add(actorModel);
            }
            keyIndex++;
        }
        NotificationActorsModel[] actorsModels = new NotificationActorsModel[actorsList.size()];
        result = actorsService.insert(actorsList.toArray(actorsModels));
        if(result != actorsModels.length) throw new RuntimeException("写入失败");
        return result;
    }

    @Override
    public int updateIsReadByReceiveId(Long receiveId, Boolean isRead, Long updatedTime, Long updatedIp) {
        return notificationDao.updateIsReadByReceiveId(receiveId,isRead,updatedTime,updatedIp);
    }

    @Override
    public int updateIsDeletedByIdAndReceiveId(Long id,Long receiveId, Boolean isDeleted, Long updatedTime, Long updatedIp) {
        return notificationDao.updateIsDeletedByIdAndReceiveId(id,receiveId,isDeleted,updatedTime,updatedIp);
    }

    @Override
    public List<NotificationDetailModel> search(Long id, EntityAction action, EntityType type, Integer actorsCount, Boolean isDeleted, Boolean isRead, Long receiveId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<NotificationDetailModel> notificationModels = notificationDao.search(id,action,type,actorsCount,isDeleted,isRead,receiveId,beginTime,endTime,ip,order,offset,limit);
        List<OperationalModel> operationalModels = new ArrayList<>();
        for (NotificationDetailModel notificationModel : notificationModels){
            operationalModels.add(new OperationalModel(notificationModel.getAction(),notificationModel.getType(),notificationModel.getTargetId(),null,notificationModel.getReceiveId(),notificationModel.getCreatedTime(),notificationModel.getCreatedIp()));
        }
        List<OperationalDetailModel> resultModels = operationalService.toDetail(operationalModels,receiveId);
        for (int i = 0; i < resultModels.size(); i++) {
            notificationModels.get(i).setTarget(resultModels.get(i).getTarget());
        }
        return notificationModels;
    }

    @Override
    public int count(Long id, EntityAction action, EntityType type, Boolean isDeleted, Boolean isRead, Long receiveId, Long beginTime, Long endTime, Long ip) {
        return notificationDao.count(id,action,type,isDeleted,isRead,receiveId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<NotificationDetailModel> page(EntityAction action, EntityType type, Integer actorsCount, Boolean isDeleted, Boolean isRead, Long receiveId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<NotificationDetailModel> data = search(null,action,type,actorsCount,isDeleted,isRead,receiveId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(null,action,type,isDeleted,isRead,receiveId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public List<NotificationGroupCountModel> groupCount(Long id, Boolean isDeleted, Boolean isRead, Long receiveId, Long beginTime, Long endTime, Long ip) {
        return notificationDao.groupCount(id,null,null,isDeleted,isRead,receiveId,beginTime,endTime,ip);
    }
}
