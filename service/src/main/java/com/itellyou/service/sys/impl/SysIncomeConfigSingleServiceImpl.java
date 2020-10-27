package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysIncomeConfigDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysIncomeConfigModel;
import com.itellyou.service.sys.SysIncomeConfigSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class SysIncomeConfigSingleServiceImpl implements SysIncomeConfigSingleService {

    private final SysIncomeConfigDao configDao;

    public SysIncomeConfigSingleServiceImpl(SysIncomeConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    public List<SysIncomeConfigModel> search(Collection<Long> ids, String name,Boolean isDeleted, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetch(CacheKeys.SYS_INCOME_CONFIG,SysIncomeConfigModel.class,ids,(Collection<Long> fetchIds) ->
                        configDao.search(fetchIds,name,isDeleted,userId,beginTime,endTime,ip,order,offset,limit));
    }

    @Override
    public int count(Collection<Long> ids, String name,Boolean isDeleted, Long userId, Long beginTime, Long endTime, Long ip) {
        return configDao.count(ids,name,isDeleted,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<SysIncomeConfigModel> page(Collection<Long> ids, String name,Boolean isDeleted, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<SysIncomeConfigModel> data = search(ids,name,isDeleted,userId,beginTime,endTime,ip,order,offset,limit);
        Integer count = count(ids,name,isDeleted,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,count,data);
    }
}
