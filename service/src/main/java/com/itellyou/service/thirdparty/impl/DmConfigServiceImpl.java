package com.itellyou.service.thirdparty.impl;

import com.itellyou.dao.thirdparty.DmConfigDao;
import com.itellyou.model.thirdparty.DmConfigModel;
import com.itellyou.service.common.ConfigMapService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@CacheConfig(cacheNames = "ali_dm_config")
@Service
public class DmConfigServiceImpl implements ConfigMapService<DmConfigModel> {

    private final DmConfigDao configDao;

    public DmConfigServiceImpl(DmConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    @Cacheable
    public DmConfigModel find(String type) {
        return configDao.get(type);
    }

    @Override
    @Cacheable(key = "#root.methodName")
    public Map<String, DmConfigModel> getMap() {
        return configDao.getAll();
    }
}
