package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysSettingDao;
import com.itellyou.model.sys.SysSettingModel;
import com.itellyou.service.sys.SysSettingService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "sys_setting")
public class SysSettingServiceImpl implements SysSettingService {

    private final SysSettingDao settingDao;

    public SysSettingServiceImpl(SysSettingDao settingDao) {
        this.settingDao = settingDao;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public SysSettingModel findByKey(String key) {
        return settingDao.findByKey(key);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public SysSettingModel findByDefault() {
        return findByKey("default");
    }

    @Override
    @CacheEvict(allEntries=true)
    public int updateByKey(SysSettingModel model) {
        return settingDao.updateByKey(model);
    }
}
