package com.itellyou.dao.sys;

import com.itellyou.model.sys.RewardConfigModel;
import com.itellyou.model.sys.RewardType;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface RewardConfigDao {

    @MapKey("type")
    Map<RewardType,RewardConfigModel> findById(String id);
}
