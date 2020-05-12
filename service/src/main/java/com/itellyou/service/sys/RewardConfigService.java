package com.itellyou.service.sys;

import com.itellyou.model.sys.RewardConfigModel;
import com.itellyou.model.sys.RewardType;

import java.util.Map;

public interface RewardConfigService {
    Map<RewardType,RewardConfigModel> findById(String id);

    Map<RewardType,RewardConfigModel> findByDefault();
}
