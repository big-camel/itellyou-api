package com.itellyou.service.user.bank.impl;

import com.itellyou.dao.user.UserBankConfigDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserBankConfigModel;
import com.itellyou.model.user.UserBankType;
import com.itellyou.service.user.bank.UserBankConfigService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@CacheConfig(cacheNames = CacheKeys.CREDIT_CONFIG_KEY)
@Service
public class UserBankConfigServiceImpl implements UserBankConfigService {

    private final UserBankConfigDao configDao;

    public UserBankConfigServiceImpl(UserBankConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    @CacheEvict(allEntries = true)
    public int insert(UserBankConfigModel model) {
        return configDao.insert(model);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int update(UserBankConfigModel model) {
        return configDao.update(model);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public UserBankConfigModel find(UserBankType bankType, EntityAction action, EntityType type) {
        return configDao.find(bankType,action,type);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public List<UserBankConfigModel> findByType(UserBankType bankType) {
        return configDao.findByType(bankType);
    }
}
