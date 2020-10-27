package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.RewardLogDao;
import com.itellyou.model.sys.*;
import com.itellyou.model.user.UserBankType;
import com.itellyou.service.sys.EntityService;
import com.itellyou.service.sys.RewardLogService;
import com.itellyou.util.CacheEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class RewardLogServiceImpl implements RewardLogService {

    private final RewardLogDao logDao;
    private final EntityService entityService;

    public RewardLogServiceImpl(RewardLogDao logDao, EntityService entityService) {
        this.logDao = logDao;
        this.entityService = entityService;
    }

    @Override
    public int insert(RewardLogModel model) {
        return logDao.insert(model);
    }

    @Override
    public List<RewardLogDetailModel> search(Long id, UserBankType bankType, EntityType dataType, Collection<Long> dataKeys,Long searchUserId, Long userId, Long createdUserId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<RewardLogModel> listModels = logDao.search(id,bankType,dataType,dataKeys,userId,createdUserId,beginTime,endTime,ip,order,offset,limit);
        EntityDataModel<CacheEntity> entityDataModel = entityService.search(listModels,(RewardLogModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            EntityType type = model.getDataType();
            Map<String,Object> args = getArgs.apply(type);
            args.put("hasContent",false);
            args.put("searchUserId",searchUserId);
            Collection ids = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!ids.contains(model.getDataKey())) ids.add(model.getDataKey());
            args.put("ids",ids);
            return new EntitySearchModel(type,args);
        },(RewardLogModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.USER);
            Collection ids = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!ids.contains(model.getCreatedUserId())) ids.add(model.getCreatedUserId());
            if(!ids.contains(model.getUserId())) ids.add(model.getUserId());
            args.put("ids",ids);
            return new EntitySearchModel(EntityType.USER,args);
        });

        //设置详细实体类信息
        List<RewardLogDetailModel> detailModels = new LinkedList<>();
        for (RewardLogModel logModel : listModels){
            RewardLogDetailModel detailModel = new RewardLogDetailModel(logModel);
            detailModel.setTargetData(entityDataModel.get(logModel.getDataType(),logModel.getDataKey()));
            detailModel.setTargetUser(entityDataModel.get(EntityType.USER,logModel.getUserId()));
            detailModel.setCreatedUser(entityDataModel.get(EntityType.USER,logModel.getCreatedUserId()));
            detailModels.add(detailModel);
        }
        return detailModels;
    }

    @Override
    public int count(Long id, UserBankType bankType, EntityType dataType, Collection<Long> dataKeys, Long userId, Long createdUserId, Long beginTime, Long endTime, Long ip) {
        return logDao.count(id,bankType,dataType,dataKeys,userId,createdUserId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<RewardLogDetailModel> page(Long id, UserBankType bankType, EntityType dataType, Collection<Long> dataKeys,Long searchUserId, Long userId, Long createdUserId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<RewardLogDetailModel> detailModels = search(id,bankType,dataType,dataKeys,searchUserId,userId,createdUserId,beginTime,endTime,ip,order,offset,limit);
        Integer count = count(id,bankType,dataType,dataKeys,userId,createdUserId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,count,detailModels);
    }

    @Override
    public RewardLogDetailModel find(Long id,Long searchUserId) {
        List<RewardLogDetailModel> detailModels = search(id,null,null,null,searchUserId,null,null,null,null,null,null,null,null);
        if(detailModels != null && detailModels.size() > 0)
            return detailModels.get(0);
        return null;
    }
}
