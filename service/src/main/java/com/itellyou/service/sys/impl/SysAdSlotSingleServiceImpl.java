package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysAdSlotDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysAdSlotModel;
import com.itellyou.service.sys.SysAdSlotSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class SysAdSlotSingleServiceImpl implements SysAdSlotSingleService {

    private final SysAdSlotDao adSlotDao;

    public SysAdSlotSingleServiceImpl(SysAdSlotDao adSlotDao) {
        this.adSlotDao = adSlotDao;
    }

    @Override
    public List<SysAdSlotModel> search(Collection<Long> ids, String name, Long adId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetch(CacheKeys.SYS_AD_SLOT,SysAdSlotModel.class,ids,fetchIds -> adSlotDao.search(fetchIds,name,adId,userId,beginTime,endTime,ip,order,offset,limit));
    }

    @Override
    public int count(Collection<Long> ids, String name, Long adId, Long userId, Long beginTime, Long endTime, Long ip) {
        return adSlotDao.count(ids,name,adId,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<SysAdSlotModel> page(Collection<Long> ids, String name, Long adId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<SysAdSlotModel> list = search(ids,name,adId,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(ids,name,adId,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,list);
    }
}
