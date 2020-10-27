package com.itellyou.service.common.impl;

import com.itellyou.dao.common.ViewInfoDao;
import com.itellyou.model.common.ViewDetailModel;
import com.itellyou.model.common.ViewInfoModel;
import com.itellyou.model.sys.EntityDataModel;
import com.itellyou.model.sys.EntitySearchModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.service.common.ViewSearchService;
import com.itellyou.service.sys.EntityService;
import com.itellyou.util.CacheEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class ViewSearchServiceImpl implements ViewSearchService {

    private final ViewInfoDao viewDao;
    private final EntityService entityService;

    public ViewSearchServiceImpl(ViewInfoDao viewDao, EntityService entityService) {
        this.viewDao = viewDao;
        this.entityService = entityService;
    }

    @Override
    public List<ViewDetailModel> search(Long id, Long userId, EntityType dataType, Long dataKey, String os, String browser, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<ViewInfoModel> list = viewDao.search(id,userId,dataType,dataKey,os,browser,beginTime,endTime,ip,order,offset,limit);

        EntityDataModel<CacheEntity> entityDataModel = entityService.search(list,(ViewInfoModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            EntityType type = model.getDataType();
            Map<String,Object> args = getArgs.apply(type);
            args.put("hasContent",false);
            Collection ids = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!ids.contains(model.getDataKey())) ids.add(model.getDataKey());
            args.put("ids",ids);
            return new EntitySearchModel(type,args);
        },(ViewInfoModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.USER);
            Collection ids = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!ids.contains(model.getCreatedUserId())) ids.add(model.getCreatedUserId());
            args.put("ids",ids);
            return new EntitySearchModel(EntityType.USER,args);
        });

        LinkedList<ViewDetailModel> detailModels = new LinkedList<>();
        for (ViewInfoModel model : list) {
            ViewDetailModel detailModel = new ViewDetailModel(model);
            detailModel.setTarget(entityDataModel.get(model.getDataType(),model.getDataKey()));
            detailModel.setUser(entityDataModel.get((EntityType.USER),model.getCreatedUserId()));
            detailModels.add(detailModel);
        }
        return detailModels;
    }

    @Override
    public List<ViewDetailModel> search(Long userId, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,userId,null,null,null,null,null,null,null,order,offset,limit);
    }

    @Override
    public List<ViewDetailModel> search(Long userId, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("updated_time","desc");
        return search(userId,order,offset,limit);
    }

    @Override
    public int count(Long id, Long userId, EntityType dataType, Long dataKey, String os, String browser, Long beginTime, Long endTime, Long ip) {
        return viewDao.count(id,userId,dataType,dataKey,os,browser,beginTime,endTime,ip);
    }

    @Override
    public PageModel<ViewDetailModel> page(Long id, Long userId, EntityType dataType, Long dataKey, String os, String browser, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<ViewDetailModel> data = search(id,userId,dataType,dataKey,os,browser,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(id,userId,dataType,dataKey,os,browser,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
