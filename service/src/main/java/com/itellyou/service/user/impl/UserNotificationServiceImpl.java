package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserNotificationDao;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.*;
import com.itellyou.service.user.UserNotificationActorsService;
import com.itellyou.service.user.UserNotificationService;
import com.itellyou.service.user.UserOperationalService;
import com.itellyou.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Service
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationDao notificationDao;
    private final UserNotificationActorsService actorsService;
    private final UserOperationalService operationalService;

    @Autowired
    public UserNotificationServiceImpl(UserNotificationDao notificationDao,UserNotificationActorsService actorsService,UserOperationalService operationalService){
        this.notificationDao = notificationDao;
        this.actorsService = actorsService;
        this.operationalService = operationalService;
    }

    @Override
    @Transactional
    public int insert(UserNotificationModel model, UserNotificationActorsModel... actors) throws Exception {
        try{
            int result = notificationDao.insert(model);
            if(result != 1) throw new Exception("写入通知失败");
            for (UserNotificationActorsModel actorsModel:actors) {
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
    public int insert(UserNotificationModel... models) {
        return notificationDao.insert(models);
    }

    @Override
    @Transactional
    public int insert(Long receiveId, UserOperationalModel... operationalModels) throws Exception {
        Map<String,UserNotificationModel> notificationModelMap = new HashMap<>();
        Map<String,List<UserNotificationActorsModel>> actorsModelMap = new HashMap<>();
        for (UserOperationalModel operationalModel:operationalModels) {
            String key = operationalModel.getAction().toString() + "_" + operationalModel.getType() + "_" + operationalModel.getTargetId();
            UserNotificationActorsModel actorsModel = new UserNotificationActorsModel(null,operationalModel.getCreatedUserId(),operationalModel.getTargetId());
            if(notificationModelMap.containsKey(key)){
                if(actorsModelMap.get(key).contains(actorsModel)){
                    continue;
                }
                actorsModelMap.get(key).add(actorsModel);
                UserNotificationModel notificationModel = notificationModelMap.get(key);
                notificationModel.setMergeCount(notificationModel.getMergeCount() + 1);
            }else{
                List<UserNotificationActorsModel> actorsModels = new ArrayList<>();
                actorsModels.add(actorsModel);
                actorsModelMap.put(key,actorsModels);

                notificationModelMap.put(key,new UserNotificationModel(false,false,receiveId,operationalModel.getAction(),operationalModel.getType(),operationalModel.getTargetId(),1, DateUtils.getTimestamp(),operationalModel.getCreatedIp()));
            }
        }
        UserNotificationModel[] notificationModels = new UserNotificationModel[notificationModelMap.values().size()];
        int result = this.insert(notificationModelMap.values().toArray(notificationModels));
        if(result != notificationModels.length) throw new RuntimeException("写入通知失败");
        int keyIndex = 0;
        List<UserNotificationActorsModel> actorsList = new ArrayList<>();
        for (String key:notificationModelMap.keySet()) {
            for (UserNotificationActorsModel actorModel:actorsModelMap.get(key)) {
                actorModel.setNotificationId(notificationModels[keyIndex].getId());
                actorsList.add(actorModel);
            }
            keyIndex++;
        }
        UserNotificationActorsModel[] actorsModels = new UserNotificationActorsModel[actorsList.size()];
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
    public List<UserNotificationDetailModel> search(Long id, UserOperationalAction action, EntityType type, Integer actorsCount, Boolean isDeleted, Boolean isRead, Long receiveId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<UserNotificationDetailModel> notificationModels = notificationDao.search(id,action,type,actorsCount,isDeleted,isRead,receiveId,beginTime,endTime,ip,order,offset,limit);
        List<UserOperationalModel> operationalModels = new ArrayList<>();
        for (UserNotificationDetailModel notificationModel : notificationModels){
            operationalModels.add(new UserOperationalModel(notificationModel.getAction(),notificationModel.getType(),notificationModel.getTargetId(),null,notificationModel.getReceiveId(),notificationModel.getCreatedTime(),notificationModel.getCreatedIp()));
        }
        List<UserOperationalDetailModel> resultModels = operationalService.toDetail(operationalModels,receiveId);
        for (int i = 0; i < resultModels.size(); i++) {
            notificationModels.get(i).setTarget(resultModels.get(i).getTarget());
        }
        return notificationModels;
    }

    @Override
    public int count(Long id, UserOperationalAction action, EntityType type, Boolean isDeleted, Boolean isRead, Long receiveId, Long beginTime, Long endTime, Long ip) {
        return notificationDao.count(id,action,type,isDeleted,isRead,receiveId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<UserNotificationDetailModel> page(UserOperationalAction action, EntityType type, Integer actorsCount, Boolean isDeleted, Boolean isRead, Long receiveId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<UserNotificationDetailModel> data = search(null,action,type,actorsCount,isDeleted,isRead,receiveId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(null,action,type,isDeleted,isRead,receiveId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public List<UserNotificationGroupCountModel> groupCount(Long id, Boolean isDeleted, Boolean isRead, Long receiveId, Long beginTime, Long endTime, Long ip) {
        return notificationDao.groupCount(id,null,null,isDeleted,isRead,receiveId,beginTime,endTime,ip);
    }
}
