package com.itellyou.service.sys;

import com.itellyou.model.sys.*;

import java.util.List;
import java.util.Map;

public interface SysPermissionService {
    int insert(SysPermissionModel model);

    int delete(String name);

    int updateByName(SysPermissionModel model);

    SysPermissionModel findByName(String name);

    List<SysPermissionModel> search(Long userId,SysPermissionPlatform platform, SysPermissionType type, SysPermissionMethod method, String name, Map<String,String> order, Integer offset, Integer limit);

    List<SysPermissionModel> search(SysPermissionType type, SysPermissionMethod method);

    List<SysPermissionModel> search(Long userId,SysPermissionPlatform platform);

    int count(Long userId,SysPermissionPlatform platform, SysPermissionType type, SysPermissionMethod method, String name);

    PageModel<SysPermissionModel> page(Long userId,SysPermissionPlatform platform, SysPermissionType type, SysPermissionMethod method, String name, Map<String,String> order, Integer offset, Integer limit);
}
