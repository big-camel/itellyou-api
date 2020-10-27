package com.itellyou.service.user;

import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserActivityDetailModel;
import com.itellyou.model.user.UserActivityModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserActivityService {

    int insert(UserActivityModel model);

    List<UserActivityDetailModel> search(Map<EntityAction, Collection<EntityType>> actionsMap,
                                         Long targetUserId,
                                        Long userId,
                                         Long searchUserId,
                                        Long beginTime, Long endTime,
                                        Long ip,
                                        Map<String, String> order,
                                        Integer offset,
                                        Integer limit);
    int count(Map<EntityAction, Collection<EntityType>> actionsMap,
              Long targetUserId,
              Long userId,
              Long beginTime,Long endTime,
              Long ip);

    PageModel<UserActivityDetailModel> page(Map<EntityAction, Collection<EntityType>> actionsMap,
                                            Long targetUserId,
                                           Long userId,
                                           Long searchUserId,
                                           Long beginTime, Long endTime,
                                           Long ip,
                                           Map<String, String> order,
                                           Integer offset,
                                           Integer limit);

    int delete(EntityAction action, EntityType type,Long targetId,Long userId);
}
