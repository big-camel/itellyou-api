package com.itellyou.service.user;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.*;
import org.apache.ibatis.annotations.Param;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface UserOperationalService {
    int insert(UserOperationalModel model);
    void insertAsync(UserOperationalModel model);
    List<UserOperationalModel> search(Long id,
                                        Map<UserOperationalAction, HashSet<EntityType>> actionsMap,
                                        Long targetUserId,
                                        Long userId,
                                        Boolean includeSelf,
                                                  Long beginTime,Long endTime,
                                                  Long ip,
                                                  Map<String, String> order,
                                                  Integer offset,
                                                  Integer limit);
    int count(Long id,
              Map<UserOperationalAction, HashSet<EntityType>> actionsMap,
              Long targetUserId,
              Long userId,
              Boolean includeSelf,
                    Long beginTime, Long endTime,
                    Long ip);

    List<UserOperationalDetailModel> toDetail(List<UserOperationalModel> models, Long searchUserId);

    List<UserOperationalDetailModel> searchDetail(Long id,
                                                  Map<UserOperationalAction, HashSet<EntityType>> actionsMap, Long targetUserId,
                                                  Long userId,
                                                  Boolean includeSelf,
                                                  Long beginTime, Long endTime,
                                                  Long ip,
                                                  Map<String, String> order,
                                                  Integer offset,
                                                  Integer limit);

    int deleteByTargetId(UserOperationalAction action,
                         EntityType type,
                         Long userId,
                         Long targetId);

    void deleteByTargetIdAsync(UserOperationalAction action,
                         EntityType type,
                         Long userId,
                         Long targetId);
}
