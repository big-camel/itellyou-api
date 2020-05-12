package com.itellyou.dao.common;

import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.sys.EntityType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface OperationalDao {
    int insert(OperationalModel model);

    List<OperationalModel> search(@Param("id") Long id,
                                  @Param("actionsMap") Map<EntityAction, HashSet<EntityType>> actionsMap,
                                  @Param("targetUserId") Long targetUserId,
                                  @Param("userId") Long userId,
                                  @Param("includeSelf") Boolean includeSelf,
                                  @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                  @Param("ip") Long ip,
                                  @Param("order") Map<String, String> order,
                                  @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);
    int count(@Param("id") Long id,
                    @Param("actionsMap") Map<EntityAction, HashSet<EntityType>> actionsMap,
              @Param("targetUserId") Long targetUserId,
              @Param("userId") Long userId,
              @Param("includeSelf") Boolean includeSelf,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);

    /**
     * 查询用户操作信息
     * @param action 操作类型
     * @param type  操作对象类型
     * @param userId 创建者 id
     * @param targetId 操作对象 id
     * @return OperationalModel 列表
     */
    List<OperationalModel> findByTargetId(@Param("action") EntityAction action,
                                          @Param("type") EntityType type,
                                          @Param("userId") Long userId,
                                          @Param("targetId") Long targetId);

    /**
     * 更新操作时间
     * @param id 操作编号
     * @param time 操作时间
     * @return 受影响行数
     */
    int updateTime(@Param("id") Long id,@Param("time") Long time);

    int deleteByTargetId(@Param("action") EntityAction action,
                                         @Param("type") EntityType type,
                                         @Param("userId") Long userId,
                                         @Param("targetId") Long targetId);
}
