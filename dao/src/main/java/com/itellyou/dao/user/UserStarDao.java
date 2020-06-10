package com.itellyou.dao.user;

import com.itellyou.model.user.UserStarModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserStarDao {
    int insert(UserStarModel draftModel);
    int delete(@Param("userId") Long userId, @Param("followerId") Long followerId);
    List<UserStarModel> search(@Param("userIds") HashSet<Long> userIds,
                                     @Param("followerId") Long followerId,
                                     @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                     @Param("ip") Long ip,
                                     @Param("order") Map<String, String> order,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);
    int count(@Param("userIds") HashSet<Long> userIds,
              @Param("followerId") Long followerId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
