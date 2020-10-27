package com.itellyou.service.user.bank.impl;

import com.itellyou.dao.user.UserWithdrawConfigDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.user.UserWithdrawConfigModel;
import com.itellyou.service.user.bank.UserWithdrawConfigService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = CacheKeys.WITHDRAW_CONFIG_KEY)
@Service
public class UserWithdrawConfigServiceImpl implements UserWithdrawConfigService {

    private final UserWithdrawConfigDao configDao;

    public UserWithdrawConfigServiceImpl(UserWithdrawConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    @Cacheable(key = "#root.methodName",unless = "#result == null")
    public UserWithdrawConfigModel getDefault() {
        return configDao.getDefault();
    }
}
