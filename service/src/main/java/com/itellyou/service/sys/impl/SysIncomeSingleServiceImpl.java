package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysIncomeDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysIncomeModel;
import com.itellyou.service.sys.SysIncomeSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class SysIncomeSingleServiceImpl implements SysIncomeSingleService {

    private final SysIncomeDao incomeDao;

    public SysIncomeSingleServiceImpl(SysIncomeDao incomeDao) {
        this.incomeDao = incomeDao;
    }

    @Override
    public List<SysIncomeModel> search(Collection<Long> ids, Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetch(CacheKeys.SYS_INCOME, SysIncomeModel.class,ids,(Collection<Long> fetchIds) ->
                incomeDao.search(fetchIds,userId,beginDate,endDate,beginTime,endTime,ip,order,offset,limit));
    }

    @Override
    public int count(Collection<Long> ids, Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime, Long ip) {
        return incomeDao.count(ids,userId,beginDate,endDate,beginTime,endTime,ip);
    }

    @Override
    public PageModel<SysIncomeModel> page(Collection<Long> ids, Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<SysIncomeModel> data = search(ids,userId,beginDate,endDate,beginTime,endTime,ip,order,offset,limit);
        Integer count = count(ids,userId,beginDate,endDate,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,count,data);
    }

    @Override
    public SysIncomeModel findByDate(Long date) {
        List<SysIncomeModel> data = search(null,null,date,date,null,null,null,null,0,1);
        return data != null && data.size() > 0 ? data.get(0) : null;
    }

    @Override
    public SysIncomeModel find(Long id) {
        List<SysIncomeModel> data = search(new HashSet<Long>(){{add(id);}},null,null,null,null,null,null,null,null,null);
        return data != null && data.size() > 0 ? data.get(0) : null;
    }
}
