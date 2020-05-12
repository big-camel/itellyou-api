package com.itellyou.dao.common;

import com.itellyou.model.common.NotificationQueueModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface NotificationQueueDao {
    int insert(NotificationQueueModel model);

    List<NotificationQueueModel> search(@Param("id") Long id,
                                  @Param("actionsMap") Map<EntityAction, HashSet<EntityType>> actionsMap,
                                  @Param("targetUserId") Long targetUserId,
                                  @Param("userId") Long userId,
                                  @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                  @Param("ip") Long ip,
                                  @Param("order") Map<String, String> order,
                                  @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);
    int count(@Param("id") Long id,
              @Param("actionsMap") Map<EntityAction, HashSet<EntityType>> actionsMap,
              @Param("targetUserId") Long targetUserId,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);

    /**
     * 查询用户操作信息
     * @param action 操作类型
     * @param type  操作对象类型
     * @param userId 创建者 id
     * @param targetId 操作对象 id
     * @return NotificationQueueModel 列表
     */
    List<NotificationQueueModel> find(@Param("action") EntityAction action,
                                          @Param("type") EntityType type,
                                          @Param("userId") Long userId,
                                          @Param("targetId") Long targetId);

    /**
     * 更新操作时间
     * @return 受影响行数
     */
    int update(@Param("id") Long id,@Param("createdTime") Long createdTime,@Param("createdIp") Long createdIp);

    List<NotificationQueueModel> searchFollowUser(
            @Param("userId") Long userId,
            @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
            @Param("ip") Long ip,
            @Param("order") Map<String, String> order,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);

    List<NotificationQueueModel> searchPublishAnswer(
                                      @Param("userId") Long userId,
                                      @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                      @Param("ip") Long ip,
                                      @Param("order") Map<String, String> order,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);

    List<NotificationQueueModel> searchPublishArticle(
            @Param("userId") Long userId,
            @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
            @Param("ip") Long ip,
            @Param("order") Map<String, String> order,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);
}
