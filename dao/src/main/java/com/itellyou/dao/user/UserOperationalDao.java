package com.itellyou.dao.user;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserOperationalDao {
    int insert(UserOperationalModel model);

    List<UserOperationalModel> search(@Param("id") Long id,
                                            @Param("actionsMap") Map<UserOperationalAction, HashSet<EntityType>> actionsMap,
                                            @Param("targetUserId") Long targetUserId,
                                            @Param("userId") Long userId,
                                            @Param("includeSelf") Boolean includeSelf,
                                            @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                            @Param("ip") Long ip,
                                            @Param("order") Map<String, String> order,
                                            @Param("offset") Integer offset,
                                            @Param("limit") Integer limit);
    int count(@Param("id") Long id,
                    @Param("actionsMap") Map<UserOperationalAction, HashSet<EntityType>> actionsMap,
              @Param("targetUserId") Long targetUserId,
              @Param("userId") Long userId,
              @Param("includeSelf") Boolean includeSelf,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);

    int deleteByTargetId(@Param("action") UserOperationalAction action,
                                         @Param("type") EntityType type,
                                         @Param("userId") Long userId,
                                         @Param("targetId") Long targetId);
}
