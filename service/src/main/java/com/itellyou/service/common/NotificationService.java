package com.itellyou.service.common;

import com.itellyou.model.common.*;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.PageModel;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    int insert(NotificationModel model, NotificationActorsModel...actors) throws Exception;

    int insert(NotificationModel... models);

    int insert(Long receiveId, NotificationQueueModel... queueModels) throws Exception;

    int updateIsReadByReceiveId(Long receiveId,Boolean isRead,Long updatedTime,Long updatedIp);

    int updateIsDeletedByIdAndReceiveId(Long id,Long receiveId,Boolean isDeleted,Long updatedTime,Long updatedIp);


    List<NotificationDetailModel> search(Long id,
                                         EntityAction action,
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
              EntityAction action,
              EntityType type,
              Boolean isDeleted,
              Boolean isRead,
              Long receiveId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<NotificationDetailModel> page(EntityAction action,
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

    List<NotificationGroupCountModel> groupCount(Long id,
                                                 Boolean isDeleted,
                                                 Boolean isRead,
                                                 Long receiveId,
                                                 Long beginTime, Long endTime,
                                                 Long ip);
}
