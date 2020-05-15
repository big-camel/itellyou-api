package com.itellyou.service.user;

public interface UserPermissionService {

    boolean check(Long userId,String permissionName);
}
