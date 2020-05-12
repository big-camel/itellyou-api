package com.itellyou.service.sys;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.sys.SysRolePermissionModel;

import java.util.List;
import java.util.Map;

public interface SysRolePermissionService {

    int insert (SysRolePermissionModel model);

    int delete(Long userId,Long roleId,String permissionName);

    int deleteByRoleId(Long userId,Long roleId);

    List<SysRoleModel> findRoleByName(String permissionName);

    List<SysRolePermissionModel> findByRoleId(Long roleId);

    List<SysRolePermissionModel> search(Long roleId,
                                        String permissionName,
                                        Long userId,
                                        Long beginTime, Long endTime,
                                        Long ip,
                                        Map<String, String> order,
                                        Integer offset,
                                        Integer limit);

    int count(Long roleId,
              String permissionName,
              Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<SysRolePermissionModel> page(Long roleId,
                                           String permissionName,
                                           Long userId,
                                           Long beginTime, Long endTime,
                                           Long ip,
                                           Map<String, String> order,
                                           Integer offset,
                                           Integer limit);
}
