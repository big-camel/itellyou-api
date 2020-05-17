package com.itellyou.dao.sys;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.RewardLogModel;
import com.itellyou.model.user.UserBankType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface RewardLogDao {
    int insert(RewardLogModel model);

    List<RewardLogModel> search(@Param("id") Long id,
                                @Param("bankType")UserBankType bankType,
                                @Param("dataType") EntityType dataType,
                                @Param("dataKeys") HashSet<Long> dataKeys,
                                @Param("userId") Long userId,
                                @Param("createdUserId") Long createdUserId,
                                @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                @Param("ip") Long ip,
                                @Param("order") Map<String, String> order,
                                @Param("offset") Integer offset,
                                @Param("limit") Integer limit);
    int count(@Param("id") Long id,
              @Param("bankType")UserBankType bankType,
              @Param("dataType") EntityType dataType,
              @Param("dataKeys") HashSet<Long> dataKeys,
              @Param("userId") Long userId,
              @Param("createdUserId") Long createdUserId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
