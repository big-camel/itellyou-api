package com.itellyou.dao.sys;

import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.sys.SysRolePermissionModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysRolePermissionDao {

    int insert (SysRolePermissionModel model);

    int delete(@Param("userId") Long userId,@Param("roleId") Long roleId,@Param("permissionName") String permissionName);

    int deleteByRoleId(@Param("userId") Long userId,@Param("roleId") Long roleId);

    List<SysRoleModel> findRoleByName(String permissionName);

    List<SysRolePermissionModel> findByRoleId(Long roleId);

    List<SysRolePermissionModel> search(@Param("roleId") Long roleId,
                              @Param("permissionName") String permissionName,
                              @Param("userId") Long userId,
                              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                              @Param("ip") Long ip,
                              @Param("order") Map<String, String> order,
                              @Param("offset") Integer offset,
                              @Param("limit") Integer limit);

    int count(@Param("roleId") Long roleId,
              @Param("permissionName") String permissionName,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
