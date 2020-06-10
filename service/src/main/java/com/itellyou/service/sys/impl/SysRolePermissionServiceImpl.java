package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysRolePermissionDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.sys.SysRolePermissionModel;
import com.itellyou.service.sys.SysRolePermissionService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "sys_role_permission")
@Service
public class SysRolePermissionServiceImpl implements SysRolePermissionService {

    private final SysRolePermissionDao rolePermissionDao;

    public SysRolePermissionServiceImpl(SysRolePermissionDao rolePermissionDao) {
        this.rolePermissionDao = rolePermissionDao;
    }

    @Override
    @CacheEvict( allEntries = true)
    public int insert(SysRolePermissionModel model) {
        return rolePermissionDao.insert(model);
    }

    @Override
    @CacheEvict( allEntries = true)
    public int delete(Long userId,Long roleId, String permissionName) {
        return rolePermissionDao.delete(userId,roleId,permissionName);
    }

    @Override
    @CacheEvict( allEntries = true)
    public int deleteByRoleId(Long userId, Long roleId) {
        return rolePermissionDao.deleteByRoleId(userId,roleId);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public List<SysRoleModel> findRoleByName(String permissionName) {
        return rolePermissionDao.findRoleByName(permissionName);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public List<SysRolePermissionModel> findByRoleId(Long roleId) {
        return rolePermissionDao.findByRoleId(roleId);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public List<SysRolePermissionModel> search(Long roleId, String permissionName, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return rolePermissionDao.search(roleId,permissionName,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public int count(Long roleId, String permissionName, Long userId, Long beginTime, Long endTime, Long ip) {
        return rolePermissionDao.count(roleId,permissionName,userId,beginTime,endTime,ip);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public PageModel<SysRolePermissionModel> page(Long roleId, String permissionName, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;

        List<SysRolePermissionModel> data = search(roleId,permissionName,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(roleId,permissionName,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }
}
