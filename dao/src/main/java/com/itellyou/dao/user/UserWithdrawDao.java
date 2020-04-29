package com.itellyou.dao.user;

import com.itellyou.model.user.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserWithdrawDao {
    int insert(UserWithdrawModel model);

    List<UserWithdrawDetailModel> search(@Param("id") String id,
                                        @Param("status") UserPaymentStatus status,
                                        @Param("userId") Long userId,
                                        @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                        @Param("ip") Long ip,
                                        @Param("order") Map<String, String> order,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);

    int count(@Param("id") String id,
              @Param("status") UserPaymentStatus status,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);

    /**
     * 获取锁定（未处理）的金额
     * @param userId
     * @return
     */
    double getLockedCash(Long userId);

    int updateStatus(@Param("id") String id,
                     @Param("status") UserPaymentStatus status,
                     @Param("updatedUserId") Long updatedUserId,
                     @Param("updatedTime") Long UpdatedTime,
                     @Param("updatedIp") Long updatedIp);
}
