package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysAdSlotDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.SysAdSlotModel;
import com.itellyou.service.sys.SysAdSlotService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = CacheKeys.SYS_AD_SLOT)
@Service
public class SysAdSlotServiceImpl implements SysAdSlotService {

    private final SysAdSlotDao adSlotDao;

    public SysAdSlotServiceImpl(SysAdSlotDao adSlotDao) {
        this.adSlotDao = adSlotDao;
    }

    @Override
    @CacheEvict(key = "T(String).valueOf(#model.adId).concat('-ad')")
    public int insert(SysAdSlotModel model) {
        return adSlotDao.insert(model);
    }

    @Override
    @Caching(evict = {@CacheEvict(key = "#model.id"),@CacheEvict(key = "T(String).valueOf(#model.adId).concat('-ad')"),})
    public int updateById(SysAdSlotModel model) {
        return adSlotDao.updateById(model);
    }

    @Override
    @Caching(evict = {@CacheEvict(key = "#model.id"),@CacheEvict(key = "T(String).valueOf(#model.adId).concat('-ad')")})
    public int deleteById(long id) {
        return adSlotDao.deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int deleteByAdId(long adId) {
        return adSlotDao.deleteByAdId(adId);
    }
}
