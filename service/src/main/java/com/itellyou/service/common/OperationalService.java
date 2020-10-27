package com.itellyou.service.common;

import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.common.OperationalDetailModel;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.sys.EntityType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface OperationalService {
    int insert(OperationalModel model);
    void insertAsync(OperationalModel model);
    List<OperationalModel> search(Long id,
                                  Map<EntityAction, Collection<EntityType>> actionsMap,
                                  Long targetUserId,
                                  Long userId,
                                  Boolean includeSelf,
                                  Long beginTime, Long endTime,
                                  Long ip,
                                  Map<String, String> order,
                                  Integer offset,
                                  Integer limit);
    int count(Long id,
              Map<EntityAction, Collection<EntityType>> actionsMap,
              Long targetUserId,
              Long userId,
              Boolean includeSelf,
                    Long beginTime, Long endTime,
                    Long ip);

    List<OperationalDetailModel> toDetail(List<OperationalModel> models, Long searchUserId);

    List<OperationalDetailModel> searchDetail(Long id,
                                              Map<EntityAction, Collection<EntityType>> actionsMap, Long targetUserId,
                                              Long userId,
                                              Boolean includeSelf,
                                              Long beginTime, Long endTime,
                                              Long ip,
                                              Map<String, String> order,
                                              Integer offset,
                                              Integer limit);

    int deleteByTargetId(EntityAction action,
                         EntityType type,
                         Long userId,
                         Long targetId);

    void deleteByTargetIdAsync(EntityAction action,
                               EntityType type,
                               Long userId,
                               Long targetId);
}
