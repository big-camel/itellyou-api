package com.itellyou.dao.reward;

import com.itellyou.model.reward.RewardConfigModel;
import com.itellyou.model.reward.RewardType;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface RewardConfigDao {

    @MapKey("type")
    Map<RewardType,RewardConfigModel> findById(String id);
}
