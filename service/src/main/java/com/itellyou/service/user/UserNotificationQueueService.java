package com.itellyou.service.user;

import com.itellyou.model.user.UserOperationalModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserNotificationQueueService {

    List<UserOperationalModel> searchFollowUser(
            Long userId,
            Long beginTime,Long endTime,
            Long ip,
            Map<String, String> order,
            Integer offset,
            Integer limit);

    List<UserOperationalModel> searchPublishAnswer(
            Long userId,
            Long beginTime,Long endTime,
            Long ip,
            Map<String, String> order,
            Integer offset,
            Integer limit);

    List<UserOperationalModel> searchPublishArticle(
            Long userId,
            Long beginTime,Long endTime,
            Long ip,
            Map<String, String> order,
            Integer offset,
            Integer limit);
}
