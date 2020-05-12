package com.itellyou.dao.user;

import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserActivityModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserActivityDao {
    int insert(UserActivityModel model);

    List<UserActivityModel> search(@Param("actionsMap") Map<EntityAction, HashSet<EntityType>> actionsMap,
                                         @Param("targetUserId") Long targetUserId,
                                         @Param("userId") Long userId,
                                         @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                         @Param("ip") Long ip,
                                         @Param("order") Map<String, String> order,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);
    int count(@Param("actionsMap") Map<EntityAction, HashSet<EntityType>> actionsMap,
              @Param("targetUserId") Long targetUserId,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);

    int delete(@Param("action") EntityAction action,
                         @Param("type") EntityType type,
                         @Param("targetId") Long targetId,@Param("userId") Long userId);
}
