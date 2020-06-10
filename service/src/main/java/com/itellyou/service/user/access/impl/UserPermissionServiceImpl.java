package com.itellyou.service.user.access.impl;

import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.service.sys.SysRolePermissionService;
import com.itellyou.service.user.access.UserPermissionService;
import com.itellyou.service.user.access.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    private final UserRoleService userRoleService;
    private final SysRolePermissionService sysRolePermissionService;

    public UserPermissionServiceImpl(UserRoleService userRoleService, SysRolePermissionService sysRolePermissionService) {
        this.userRoleService = userRoleService;
        this.sysRolePermissionService = sysRolePermissionService;
    }

    @Override
    public boolean check(Long userId, String permissionName) {
        List<SysRoleModel> userRoleModels = userRoleService.findRoleByUserId(userId,true);
        List<SysRoleModel> sysRoleModels = sysRolePermissionService.findRoleByName(permissionName);
        for (SysRoleModel roleModel : sysRoleModels){
            for (SysRoleModel userRole : userRoleModels){
                if(userRole.getId().equals(roleModel.getId())) return true;
            }
        }
        return false;
    }
}
