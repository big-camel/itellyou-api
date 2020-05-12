package com.itellyou.service.sys;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.RewardLogModel;
import com.itellyou.model.user.UserBankType;

public interface RewardService {
    RewardLogModel doReward(UserBankType bankType, Double amount, EntityType dataType, Long dataKey, Long userId, Long ip) throws Exception;
}
