package com.itellyou.service.statistics.impl;

import com.itellyou.model.statistics.StatisticsDetailModel;
import com.itellyou.model.statistics.StatisticsInfoModel;
import com.itellyou.model.sys.EntityDataModel;
import com.itellyou.model.sys.EntitySearchModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.service.statistics.StatisticsSearchService;
import com.itellyou.service.statistics.StatisticsSingleService;
import com.itellyou.service.sys.EntityService;
import com.itellyou.util.CacheEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class StatisticsSearchServiceImpl implements StatisticsSearchService {

    private final StatisticsSingleService singleService;
    private final EntityService entityService;

    public StatisticsSearchServiceImpl(StatisticsSingleService singleService, EntityService entityService) {
        this.singleService = singleService;
        this.entityService = entityService;
    }

    @Override
    public List<StatisticsDetailModel> search(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<StatisticsInfoModel> infoModels = singleService.search(userId,dataType,dataKeys,beginDate,endDate,beginTime,endTime,order,offset,limit);
        List<StatisticsDetailModel> detailModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;
        EntityDataModel<CacheEntity> entityDataModel = entityService.search(infoModels,(StatisticsInfoModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(model.getDataType());
            Collection ids = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!ids.contains(model.getDataKey())) ids.add(model.getDataKey());
            args.put("ids",ids);
            return new EntitySearchModel(model.getDataType(),args);
        }, (StatisticsInfoModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.USER);
            Collection authorIds = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!authorIds.contains(model.getUserId())) authorIds.add(model.getUserId());
            args.put("ids",authorIds);
            return new EntitySearchModel(EntityType.USER,args);
        });
        for (StatisticsInfoModel infoModel : infoModels){
            StatisticsDetailModel detailModel = new StatisticsDetailModel(infoModel);
            detailModel.setTarget(entityDataModel.get(infoModel.getDataType(),infoModel.getDataKey()));
            detailModel.setUser( entityDataModel.get(EntityType.USER,infoModel.getDataKey()));
        }
        return detailModels;
    }

    @Override
    public PageModel<StatisticsDetailModel> page(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<StatisticsDetailModel> list = search(userId,dataType,dataKeys,beginDate,endDate,beginTime,endTime,order,offset,limit);
        Integer count = singleService.count(userId,dataType,dataKeys,beginDate,endDate,beginTime,endTime);
        return new PageModel<>(offset,limit,count,list);
    }
}
