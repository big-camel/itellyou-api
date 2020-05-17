package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.RewardLogDao;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.RewardLogDetailModel;
import com.itellyou.model.sys.RewardLogModel;
import com.itellyou.model.user.UserBankType;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.sys.EntityService;
import com.itellyou.service.sys.RewardLogService;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public List<RewardLogDetailModel> search(Long id, UserBankType bankType, EntityType dataType, HashSet<Long> dataKeys,Long searchUserId, Long userId, Long createdUserId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<RewardLogModel> listModels = logDao.search(id,bankType,dataType,dataKeys,userId,createdUserId,beginTime,endTime,ip,order,offset,limit);
        Map<EntityType, HashSet<Long>> mapIds = new HashMap<>();
        for (RewardLogModel logModel : listModels){
            if(!mapIds.containsKey(logModel.getDataType())){
                mapIds.put(logModel.getDataType(),new HashSet<>());
            }
            if(!mapIds.containsKey(EntityType.USER)){
                mapIds.put(EntityType.USER,new HashSet<>());
            }
            if(!mapIds.get(logModel.getDataType()).contains(logModel.getDataKey()))
                mapIds.get(logModel.getDataType()).add(logModel.getDataKey());
            if(!mapIds.get(EntityType.USER).contains(logModel.getUserId()))
                mapIds.get(EntityType.USER).add(logModel.getUserId());
            if(!mapIds.get(EntityType.USER).contains(logModel.getCreatedUserId()))
                mapIds.get(EntityType.USER).add(logModel.getCreatedUserId());
        }
        Map<EntityType,Map<Long,Object>> detailData = entityService.find(mapIds,searchUserId,0);
        List<RewardLogDetailModel> detailModels = new ArrayList<>();
        for (RewardLogModel logModel : listModels){
            RewardLogDetailModel detailModel = new RewardLogDetailModel(logModel);
            Map<Long,Object> dataMap = detailData.get(logModel.getDataType());
            if(dataMap != null){
                detailModel.setTargetData(dataMap.get(logModel.getDataKey()));
            }
            dataMap = detailData.get(EntityType.USER);
            if(dataMap != null && dataMap.get(logModel.getUserId()) != null){
                detailModel.setTargetUser((UserDetailModel) dataMap.get(logModel.getUserId()));
            }
            if(dataMap != null && dataMap.get(logModel.getCreatedUserId()) != null){
                detailModel.setCreatedUser((UserDetailModel) dataMap.get(logModel.getCreatedUserId()));
            }
            detailModels.add(detailModel);
        }
        return detailModels;
    }

    @Override
    public int count(Long id, UserBankType bankType, EntityType dataType, HashSet<Long> dataKeys, Long userId, Long createdUserId, Long beginTime, Long endTime, Long ip) {
        return logDao.count(id,bankType,dataType,dataKeys,userId,createdUserId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<RewardLogDetailModel> page(Long id, UserBankType bankType, EntityType dataType, HashSet<Long> dataKeys,Long searchUserId, Long userId, Long createdUserId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
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
