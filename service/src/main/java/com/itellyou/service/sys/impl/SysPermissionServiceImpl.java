package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysPermissionDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.*;
import com.itellyou.service.sys.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = CacheKeys.SYS_PERMISSION_KEY)
@Service
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysPermissionDao permissionDao;

    @Autowired
    public SysPermissionServiceImpl(SysPermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }

    @Override
    @CacheEvict( allEntries = true)
    public int insert(SysPermissionModel model) {
        return permissionDao.insert(model);
    }

    @Override
    @CacheEvict( allEntries = true)
    public int delete(String name) {
        return permissionDao.delete(name);
    }

    @Override
    @CacheEvict( allEntries = true)
    public int updateByName(SysPermissionModel model) {
        return permissionDao.updateByName(model);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public SysPermissionModel findByName(String name) {
        return permissionDao.findByName(name);
    }

    @Override
    public List<SysPermissionModel> search(Long userId,SysPermissionPlatform platform,SysPermissionType type, SysPermissionMethod method, String name, Map<String, String> order, Integer offset, Integer limit) {
        return permissionDao.search(userId,platform,type,method,name,order,offset,limit);
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#type).concat('-').concat(#method)" ,unless = "#result == null")
    public List<SysPermissionModel> search(SysPermissionType type, SysPermissionMethod method) {
        return search(null,null,type,method,null,null,null,null);
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#userId).concat('-').concat(#platform)", unless = "#result == null")
    public List<SysPermissionModel> search(Long userId,SysPermissionPlatform platform) {
        return search(userId,platform,null,null,null,null,null,null);
    }

    @Override
    public int count(Long userId,SysPermissionPlatform platform, SysPermissionType type, SysPermissionMethod method, String name) {
        return permissionDao.count(userId,platform,type,method,name);
    }

    @Override
    public PageModel<SysPermissionModel> page(Long userId,SysPermissionPlatform platform, SysPermissionType type, SysPermissionMethod method, String name, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<SysPermissionModel> data = search(userId,platform,type,method,name,order,offset,limit);
        Integer total = count(userId,platform,type,method,name);
        return new PageModel<>(offset,limit,total,data);
    }
}
