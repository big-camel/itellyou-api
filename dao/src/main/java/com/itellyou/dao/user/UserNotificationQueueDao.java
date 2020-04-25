package com.itellyou.dao.user;

import com.itellyou.model.user.UserOperationalAction;
import com.itellyou.model.user.UserOperationalModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserNotificationQueueDao {
    List<UserOperationalModel> searchFollowUser(
            @Param("userId") Long userId,
            @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
            @Param("ip") Long ip,
            @Param("order") Map<String, String> order,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);

    List<UserOperationalModel> searchPublishAnswer(
                                      @Param("userId") Long userId,
                                      @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                      @Param("ip") Long ip,
                                      @Param("order") Map<String, String> order,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);

    List<UserOperationalModel> searchPublishArticle(
            @Param("userId") Long userId,
            @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
            @Param("ip") Long ip,
            @Param("order") Map<String, String> order,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);
}
