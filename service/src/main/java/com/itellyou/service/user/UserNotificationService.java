package com.itellyou.service.user;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.*;

import java.util.List;
import java.util.Map;

public interface UserNotificationService {

    int insert(UserNotificationModel model,UserNotificationActorsModel...actors) throws Exception;

    int insert(UserNotificationModel... models);

    int insert(Long receiveId, UserOperationalModel... queueModels) throws Exception;

    int updateIsReadByReceiveId(Long receiveId,Boolean isRead,Long updatedTime,Long updatedIp);

    int updateIsDeletedByIdAndReceiveId(Long id,Long receiveId,Boolean isDeleted,Long updatedTime,Long updatedIp);


    List<UserNotificationDetailModel> search(Long id,
                                             UserOperationalAction action,
                                             EntityType type,
                                             Integer actorsCount,
                                             Boolean isDeleted,
                                             Boolean isRead,
                                             Long receiveId,
                                             Long beginTime, Long endTime,
                                             Long ip,
                                             Map<String, String> order,
                                             Integer offset,
                                             Integer limit);
    int count(Long id,
              UserOperationalAction action,
              EntityType type,
              Boolean isDeleted,
              Boolean isRead,
              Long receiveId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<UserNotificationDetailModel> page(UserOperationalAction action,
                                                EntityType type,
                                                Integer actorsCount,
                                                Boolean isDeleted,
                                                Boolean isRead,
                                                Long receiveId,
                                                Long beginTime, Long endTime,
                                                Long ip,
                                                Map<String, String> order,
                                                Integer offset,
                                                Integer limit);

    List<UserNotificationGroupCountModel> groupCount(Long id,
                   Boolean isDeleted,
                   Boolean isRead,
                   Long receiveId,
                   Long beginTime, Long endTime,
                   Long ip);
}
