package com.itellyou.service.thirdparty.impl;

import com.itellyou.dao.thirdparty.AliConfigDao;
import com.itellyou.model.thirdparty.AliConfigModel;
import com.itellyou.service.common.ConfigDefaultService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "ali_config")
@Service
public class AliConfigDefaultServiceImpl implements ConfigDefaultService<AliConfigModel> {

    private final AliConfigDao aliConfigDao;

    public AliConfigDefaultServiceImpl(AliConfigDao aliConfigDao) {
        this.aliConfigDao = aliConfigDao;
    }

    @Override
    @Cacheable(key = "#root.methodName")
    public AliConfigModel getDefault() {
        return aliConfigDao.get();
    }
}
