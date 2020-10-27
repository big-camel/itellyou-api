package com.itellyou.service.user.bank.impl;

import com.itellyou.dao.user.UserBankLogDao;
import com.itellyou.model.sys.*;
import com.itellyou.model.user.UserBankLogDetailModel;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankType;
import com.itellyou.service.sys.EntityService;
import com.itellyou.service.user.bank.UserBankLogSearchService;
import com.itellyou.service.user.bank.UserBankLogSingleService;
import com.itellyou.util.CacheEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class UserBankLogSearchServiceImpl implements UserBankLogSearchService {

    private final UserBankLogDao userBankLogDao;
    private final EntityService entityService;
    private final UserBankLogSingleService logSingleService;

    @Autowired
    public UserBankLogSearchServiceImpl(UserBankLogDao userBankLogDao, EntityService entityService, UserBankLogSingleService logSingleService){
        this.userBankLogDao = userBankLogDao;
        this.entityService = entityService;
        this.logSingleService = logSingleService;
    }

    @Override
    public List<UserBankLogDetailModel> search(Collection<Long> ids,UserBankType type, EntityAction action,EntityType dataType,String dataKey, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<UserBankLogModel> list = logSingleService.search(ids,type,action,dataType,dataKey,userId,beginTime,endTime,ip,order,offset,limit);
        EntityDataModel<CacheEntity> entityDataModel = entityService.search(list,(UserBankLogModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            EntityType entityType = model.getDataType();
            Map<String,Object> args = getArgs.apply(entityType);
            args.put("hasContent",false);
            Collection fetchIds = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!fetchIds.contains(model.getDataKey())) fetchIds.add(model.getDataKey());
            args.put("ids",fetchIds);
            return new EntitySearchModel(entityType,args);
        },(UserBankLogModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.USER);
            Collection userIds = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!userIds.contains(model.getCreatedUserId())) userIds.add(model.getCreatedUserId());
            args.put("ids",userIds);
            return new EntitySearchModel(EntityType.USER,args);
        });

        LinkedList<UserBankLogDetailModel> detailModels = new LinkedList<>();
        for (UserBankLogModel model : list) {
            UserBankLogDetailModel detailModel = new UserBankLogDetailModel(model);
            detailModel.setTarget(entityDataModel.get(model.getDataType(),model.getDataKey()));
            detailModel.setUser(entityDataModel.get(EntityType.USER,model.getCreatedUserId()));
            detailModels.add(detailModel);
        }
        return detailModels;
    }

    @Override
    public int count(Collection<Long> ids, UserBankType type, EntityAction action, EntityType dataType,String dataKey, Long userId, Long beginTime, Long endTime, Long ip) {
        return userBankLogDao.count(ids,type,action,dataType,dataKey,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<UserBankLogDetailModel> page(Collection<Long> ids, UserBankType type, EntityAction action, EntityType dataType, String dataKey, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 20;
        List<UserBankLogDetailModel> data = search(ids,type,action,dataType,dataKey,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(ids,type,action,dataType,dataKey,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }
}
