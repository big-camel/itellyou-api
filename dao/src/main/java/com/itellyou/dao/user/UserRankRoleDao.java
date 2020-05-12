package com.itellyou.dao.user;

import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.user.UserRankRoleModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserRankRoleDao {

    int insert(UserRankRoleModel model);

    int delete(@Param("rankId") Long rankId, @Param("roleId") Long roleId);

    int deleteByRoleId(Long roleId);

    List<SysRoleModel> findRoleByRankId(Long rankId);

    List<UserRankRoleModel> search(@Param("roleId") Long roleId,
                               @Param("rankId") Long rankId,
                               @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                               @Param("ip") Long ip,
                               @Param("order") Map<String, String> order,
                               @Param("offset") Integer offset,
                               @Param("limit") Integer limit);

    int count(@Param("roleId") Long roleId,
              @Param("rankId") Long rankId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
