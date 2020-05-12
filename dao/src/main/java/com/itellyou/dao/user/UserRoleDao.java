package com.itellyou.dao.user;

import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.user.UserRoleModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserRoleDao {

    int insert(UserRoleModel model);

    int delete(@Param("userId") Long userId,@Param("roleId") Long roleId);

    int deleteByRoleId(Long roleId);

    List<SysRoleModel> findRoleByUserId(Long userId);

    List<UserRoleModel> search(@Param("roleId") Long roleId,
                                        @Param("userId") Long userId,
                                        @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                        @Param("ip") Long ip,
                                        @Param("order") Map<String, String> order,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);

    int count(@Param("roleId") Long roleId,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
