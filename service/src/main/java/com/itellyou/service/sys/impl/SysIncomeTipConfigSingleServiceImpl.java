package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysIncomeTipConfigDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysIncomeTipConfigModel;
import com.itellyou.service.sys.SysIncomeTipConfigSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class SysIncomeTipConfigSingleServiceImpl implements SysIncomeTipConfigSingleService {

    private final SysIncomeTipConfigDao configDao;

    public SysIncomeTipConfigSingleServiceImpl(SysIncomeTipConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    public List<SysIncomeTipConfigModel> search(Collection<Long> ids, String name, EntityType dataType, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetch(CacheKeys.SYS_TIP_CONFIG, SysIncomeTipConfigModel.class,ids,(Collection<Long> fetchIds) ->
                configDao.search(fetchIds,name,dataType,userId,beginTime,endTime,ip,order,offset,limit));
    }

    @Override
    public int count(Collection<Long> ids, String name, EntityType dataType, Long userId, Long beginTime, Long endTime, Long ip) {
        return configDao.count(ids,name,dataType,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<SysIncomeTipConfigModel> page(Collection<Long> ids, String name, EntityType dataType, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<SysIncomeTipConfigModel> data = search(ids,name,dataType,userId,beginTime,endTime,ip,order,offset,limit);
        Integer count = count(ids,name,dataType,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,count,data);
    }
}
