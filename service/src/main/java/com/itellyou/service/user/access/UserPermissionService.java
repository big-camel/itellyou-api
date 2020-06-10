package com.itellyou.service.user.access;

public interface UserPermissionService {

    boolean check(Long userId,String permissionName);
}
