package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysAdSlotDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysAdSlotModel;
import com.itellyou.service.sys.SysAdSlotSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class SysAdSlotSingleServiceImpl implements SysAdSlotSingleService {

    private final SysAdSlotDao adSlotDao;

    public SysAdSlotSingleServiceImpl(SysAdSlotDao adSlotDao) {
        this.adSlotDao = adSlotDao;
    }

    @Override
    public List<SysAdSlotModel> search(Collection<Long> ids, String name,Collection<Long> adIds, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetch(CacheKeys.SYS_AD_SLOT,SysAdSlotModel.class,ids,fetchIds -> adSlotDao.search(fetchIds,name,adIds,userId,beginTime,endTime,ip,order,offset,limit));
    }

    @Override
    public int count(Collection<Long> ids, String name, Collection<Long> adIds, Long userId, Long beginTime, Long endTime, Long ip) {
        return adSlotDao.count(ids,name,adIds,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<SysAdSlotModel> page(Collection<Long> ids, String name, Collection<Long> adIds, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<SysAdSlotModel> list = search(ids,name,adIds,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(ids,name,adIds,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,list);
    }

    @Override
    public List<SysAdSlotModel> findByAdId(Long adId) {
        List<SysAdSlotModel> models = RedisUtils.get(CacheKeys.SYS_AD_SLOT,adId + "-ad",List.class);
        if(models == null) {
            models = search(null,null,new HashSet<Long>(){{ add(adId);}},null,null,null,null, null,null,null);
        }
        if(models != null){
            RedisUtils.set(CacheKeys.SYS_AD_SLOT,adId + "-ad",models);
        }
        return models;
    }

    @Override
    public SysAdSlotModel findById(Long id) {
        List<SysAdSlotModel> list = search(new HashSet<Long>(){{add(id);}},null,null,null,null,null,null,null,null,null);
        return list.size() > 0 ? list.get(0) : null;
    }
}
