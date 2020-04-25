package com.itellyou.dao.user;

import com.itellyou.model.user.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserBankLogDao {
    int insert(UserBankLogModel userBankLogModel);

    List<UserBankLogModel> search(@Param("id") String id,
                                        @Param("type") UserBankType type,
                                        @Param("dataType") UserBankLogType dataType,
                                        @Param("userId") Long userId,
                                        @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                        @Param("ip") Long ip,
                                        @Param("order") Map<String, String> order,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);

    int count(@Param("id") String id,
              @Param("type") UserBankType type,
              @Param("dataType") UserBankLogType dataType,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
