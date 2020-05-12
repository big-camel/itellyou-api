package com.itellyou.service.common;

import com.itellyou.model.common.NotificationQueueModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface NotificationQueueService {

    int insert(NotificationQueueModel model);

    List<NotificationQueueModel> search(Long id,
                                  Map<EntityAction, HashSet<EntityType>> actionsMap,
                                  Long targetUserId,
                                  Long userId,
                                  Long beginTime, Long endTime,
                                  Long ip,
                                  Map<String, String> order,
                                  Integer offset,
                                  Integer limit);
    int count(Long id,
              Map<EntityAction, HashSet<EntityType>> actionsMap,
                Long targetUserId,
              Long userId,
              Long beginTime, Long endTime,
              Long ip);

    List<NotificationQueueModel> find(EntityAction action,
                                      EntityType type,
                                      Long userId,
                                      Long targetId);

    int update(Long id,Long createdTime,Long createdIp);

    List<NotificationQueueModel> searchFollowUser(
            Long userId,
            Long beginTime,Long endTime,
            Long ip,
            Map<String, String> order,
            Integer offset,
            Integer limit);

    List<NotificationQueueModel> searchPublishAnswer(
            Long userId,
            Long beginTime,Long endTime,
            Long ip,
            Map<String, String> order,
            Integer offset,
            Integer limit);

    List<NotificationQueueModel> searchPublishArticle(
            Long userId,
            Long beginTime,Long endTime,
            Long ip,
            Map<String, String> order,
            Integer offset,
            Integer limit);
}
