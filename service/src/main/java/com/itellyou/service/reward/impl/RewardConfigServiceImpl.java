package com.itellyou.service.reward.impl;

import com.itellyou.dao.reward.RewardConfigDao;
import com.itellyou.model.reward.RewardConfigModel;
import com.itellyou.model.reward.RewardType;
import com.itellyou.service.reward.RewardConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RewardConfigServiceImpl implements RewardConfigService {

    private final RewardConfigDao rewardConfigDao;
    @Autowired
    public RewardConfigServiceImpl(RewardConfigDao rewardConfigDao){
        this.rewardConfigDao = rewardConfigDao;
    }

    @Override
    public Map<RewardType,RewardConfigModel> findById(String id) {
        return rewardConfigDao.findById(id);
    }

    @Override
    public Map<RewardType,RewardConfigModel> findByDefault() {
        return findById("default");
    }
}
