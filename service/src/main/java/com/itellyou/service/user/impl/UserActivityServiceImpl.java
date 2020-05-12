package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserActivityDao;
import com.itellyou.model.common.OperationalDetailModel;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserActivityDetailModel;
import com.itellyou.model.user.UserActivityModel;
import com.itellyou.service.common.OperationalService;
import com.itellyou.service.user.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserActivityServiceImpl implements UserActivityService {

    private final UserActivityDao activityDao;
    private final OperationalService operationalService;

    @Autowired
    public UserActivityServiceImpl(UserActivityDao activityDao, OperationalService operationalService){
        this.activityDao = activityDao;
        this.operationalService = operationalService;
    }

    @Override
    public int insert(UserActivityModel model) {
        return activityDao.insert(model);
    }

    @Override
    public List<UserActivityDetailModel> search(Map<EntityAction, HashSet<EntityType>> actionsMap, Long targetUserId, Long userId,Long searchUserId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<UserActivityModel> list = activityDao.search(actionsMap,targetUserId,userId,beginTime,endTime,ip,order,offset,limit);
        List<UserActivityDetailModel> activityDetailModels = new LinkedList<>();
        List<OperationalModel> operationModes = new LinkedList<>();
        for (UserActivityModel activityModel:list) {
            operationModes.add(new OperationalModel(activityModel.getAction(),activityModel.getType(),activityModel.getTargetId(),activityModel.getTargetUserId(),activityModel.getCreatedUserId(),activityModel.getCreatedTime(),activityModel.getCreatedIp()));
        }
        List<OperationalDetailModel> detailModels = operationalService.toDetail(operationModes,searchUserId);
        for (OperationalDetailModel detailModel : detailModels){
            for (UserActivityModel activityModel : list){
                if(detailModel.getAction().equals(activityModel.getAction()) &&
                detailModel.getType().equals(activityModel.getType()) &&
                detailModel.getTargetId().equals(activityModel.getTargetId()) &&
                detailModel.getCreatedUserId().equals(activityModel.getCreatedUserId())){
                    activityDetailModels.add(new UserActivityDetailModel(activityModel,detailModel.getTarget()));
                }
            }
        }
        return activityDetailModels;
    }

    @Override
    public int count(Map<EntityAction, HashSet<EntityType>> actionsMap, Long targetUserId, Long userId, Long beginTime, Long endTime, Long ip) {
        return activityDao.count(actionsMap,targetUserId,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<UserActivityDetailModel> page(Map<EntityAction, HashSet<EntityType>> actionsMap,Long targetUserId, Long userId,Long searchUserId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<UserActivityDetailModel> data = search(actionsMap,targetUserId,userId,searchUserId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(actionsMap,targetUserId,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public int delete(EntityAction action, EntityType type, Long targetId, Long userId) {
        return activityDao.delete(action,type,targetId,userId);
    }
}
