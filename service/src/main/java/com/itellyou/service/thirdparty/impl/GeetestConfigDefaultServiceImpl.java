package com.itellyou.service.thirdparty.impl;

import com.itellyou.dao.thirdparty.GeetestConfigDao;
import com.itellyou.model.thirdparty.GeetestConfigModel;
import com.itellyou.service.common.ConfigDefaultService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "geetest_config")
@Service
public class GeetestConfigDefaultServiceImpl implements ConfigDefaultService<GeetestConfigModel> {

    private final GeetestConfigDao geetestConfigDao;

    public GeetestConfigDefaultServiceImpl(GeetestConfigDao geetestConfigDao) {
        this.geetestConfigDao = geetestConfigDao;
    }

    @Override
    @Cacheable(key = "#root.methodName",unless = "#result == null")
    public GeetestConfigModel getDefault() {
        return geetestConfigDao.getConfig();
    }
}
