package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.RewardConfigDao;
import com.itellyou.model.sys.RewardConfigModel;
import com.itellyou.model.sys.RewardType;
import com.itellyou.service.sys.RewardConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@CacheConfig(cacheNames = "reward_config")
@Service
public class RewardConfigServiceImpl implements RewardConfigService {

    private final RewardConfigDao rewardConfigDao;
    @Autowired
    public RewardConfigServiceImpl(RewardConfigDao rewardConfigDao){
        this.rewardConfigDao = rewardConfigDao;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public Map<RewardType,RewardConfigModel> findById(String id) {
        return rewardConfigDao.findById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName",unless = "#result == null")
    public Map<RewardType,RewardConfigModel> findByDefault() {
        return findById("default");
    }
}
