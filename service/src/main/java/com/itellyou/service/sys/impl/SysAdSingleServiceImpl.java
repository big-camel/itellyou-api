package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysAdDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.AdType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysAdModel;
import com.itellyou.service.sys.SysAdSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class SysAdSingleServiceImpl implements SysAdSingleService {

    private final SysAdDao adDao;

    public SysAdSingleServiceImpl(SysAdDao adDao) {
        this.adDao = adDao;
    }

    @Override
    public List<SysAdModel> search(Collection<Long> ids, AdType type, String name, Boolean enabledForeign, Boolean enabledCn, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetch(CacheKeys.SYS_AD,SysAdModel.class,ids,fetchIds -> adDao.search(fetchIds,type,name,enabledForeign,enabledCn,userId,beginTime,endTime,ip,order,offset,limit));
    }

    @Override
    public int count(Collection<Long> ids, AdType type, String name, Boolean enabledForeign, Boolean enabledCn, Long userId, Long beginTime, Long endTime, Long ip) {
        return adDao.count(ids,type,name,enabledForeign,enabledCn,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<SysAdModel> page(Collection<Long> ids, AdType type, String name, Boolean enabledForeign, Boolean enabledCn, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<SysAdModel> list = search(ids,type,name,enabledForeign,enabledCn,userId,beginTime,endTime,ip,order,offset,limit);
        Integer count = count(ids,type,name,enabledForeign,enabledCn,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,count,list);
    }
}
