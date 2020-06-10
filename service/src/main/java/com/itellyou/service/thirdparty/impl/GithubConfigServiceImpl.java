package com.itellyou.service.thirdparty.impl;

import com.itellyou.dao.thirdparty.GithubConfigDao;
import com.itellyou.model.thirdparty.GithubConfigModel;
import com.itellyou.service.common.ConfigDefaultService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "github_config")
@Service
public class GithubConfigServiceImpl implements ConfigDefaultService<GithubConfigModel> {

    private final GithubConfigDao configDao;

    public GithubConfigServiceImpl(GithubConfigDao configDao) {
        this.configDao = configDao;
    }

    @Cacheable(key = "#root.methodName",unless = "#result == null")
    public GithubConfigModel getDefault(){
        return configDao.get();
    }
}
