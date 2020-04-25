package com.itellyou.service.reward;

import com.itellyou.model.reward.RewardConfigModel;
import com.itellyou.model.reward.RewardType;

import java.util.List;
import java.util.Map;

public interface RewardConfigService {
    Map<RewardType,RewardConfigModel> findById(String id);

    Map<RewardType,RewardConfigModel> findByDefault();
}
