package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysAdDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.SysAdModel;
import com.itellyou.service.sys.SysAdService;
import com.itellyou.service.sys.SysAdSlotService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = CacheKeys.SYS_AD)
@Service
public class SysAdServiceImpl implements SysAdService {

    private final SysAdDao adDao;
    private final SysAdSlotService adSlotService;

    public SysAdServiceImpl(SysAdDao adDao, SysAdSlotService adSlotService) {
        this.adDao = adDao;
        this.adSlotService = adSlotService;
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional
    public int insert(SysAdModel model) {
        try {
            if(model.getEnabledForeign() != null && model.getEnabledForeign()){
                updateEnabledForeignAll(false);
            }
            if(model.getEnabledCn() != null && model.getEnabledCn()){
                updateEnabledCnAll(false);
            }
            return adDao.insert(model);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    @CacheEvict(key = "#model.id")
    @Transactional
    public int updateById(SysAdModel model) {
        try {
            if(model.getEnabledForeign() != null && model.getEnabledForeign()){
                updateEnabledForeignAll(false);
            }
            if(model.getEnabledCn() != null && model.getEnabledCn()){
                updateEnabledCnAll(false);
            }
            return adDao.updateById(model);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public int updateEnabledForeignAll(boolean enabled) {
        return adDao.updateEnabledForeignAll(enabled);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int updateEnabledCnAll(boolean enabled) {
        return adDao.updateEnabledCnAll(enabled);
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional
    public int deleteById(long id) {
        try {
            adSlotService.deleteByAdId(id);
            return adDao.deleteById(id);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }
}
