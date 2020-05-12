package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserWithdrawConfigDao;
import com.itellyou.model.user.UserWithdrawConfigModel;
import com.itellyou.service.user.UserWithdrawConfigService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "withdraw_config")
@Service
public class UserWithdrawConfigServiceImpl implements UserWithdrawConfigService {

    private final UserWithdrawConfigDao configDao;

    public UserWithdrawConfigServiceImpl(UserWithdrawConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    @Cacheable(key = "#root.methodName")
    public UserWithdrawConfigModel getDefault() {
        return configDao.getDefault();
    }
}
