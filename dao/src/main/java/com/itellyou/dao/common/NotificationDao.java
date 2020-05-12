package com.itellyou.dao.common;

import com.itellyou.model.common.NotificationDetailModel;
import com.itellyou.model.common.NotificationModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.common.NotificationGroupCountModel;
import com.itellyou.model.sys.EntityType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface NotificationDao {
    int insert(@Param("models") NotificationModel... models);

    int updateIsReadByReceiveId(@Param("receiveId")Long receiveId,@Param("isRead")Boolean isRead,@Param("updatedTime") Long updatedTime,@Param("updatedIp") Long updatedIp);

    int updateIsDeletedByIdAndReceiveId(@Param("id")Long id,@Param("receiveId")Long receiveId,@Param("isDeleted")Boolean isDeleted,@Param("updatedTime") Long updatedTime,@Param("updatedIp") Long updatedIp);

    List<NotificationDetailModel> search(@Param("id") Long id,
                                         @Param("action") EntityAction action,
                                         @Param("type") EntityType type,
                                         @Param("actorsCount") Integer actorsCount,
                                         @Param("isDeleted") Boolean isDeleted,
                                         @Param("isRead") Boolean isRead,
                                         @Param("receiveId") Long receiveId,
                                         @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                         @Param("ip") Long ip,
                                         @Param("order") Map<String, String> order,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);
    int count(@Param("id") Long id,
              @Param("action") EntityAction action,
              @Param("type") EntityType type,
              @Param("isDeleted") Boolean isDeleted,
              @Param("isRead") Boolean isRead,
              @Param("receiveId") Long receiveId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);

    List<NotificationGroupCountModel> groupCount(@Param("id") Long id,
                                                 @Param("action") EntityAction action,
                                                 @Param("type") EntityType type,
                                                 @Param("isDeleted") Boolean isDeleted,
                                                 @Param("isRead") Boolean isRead,
                                                 @Param("receiveId") Long receiveId,
                                                 @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                                 @Param("ip") Long ip);
}
