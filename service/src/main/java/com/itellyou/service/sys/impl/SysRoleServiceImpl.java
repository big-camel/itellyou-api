package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysRoleDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.service.sys.SysRolePermissionService;
import com.itellyou.service.sys.SysRoleService;
import com.itellyou.service.user.UserRankRoleService;
import com.itellyou.service.user.UserRoleService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "sys_role")
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleDao roleDao;
    private final SysRolePermissionService rolePermissionService;
    private final UserRankRoleService rankRoleService;
    private final UserRoleService userRoleService;

    public SysRoleServiceImpl(SysRoleDao roleDao, SysRolePermissionService rolePermissionService, UserRankRoleService rankRoleService, UserRoleService userRoleService) {
        this.roleDao = roleDao;
        this.rolePermissionService = rolePermissionService;
        this.rankRoleService = rankRoleService;
        this.userRoleService = userRoleService;
    }

    @Override
    @CacheEvict( allEntries = true)
    public int insert(SysRoleModel model) {
        return roleDao.insert(model);
    }

    @Override
    @Cacheable
    public SysRoleModel findByName(String name,Long userId) {
        return roleDao.findByName(name,userId);
    }

    @Override
    @Cacheable
    public SysRoleModel findById(Long id) {
        return roleDao.findById(id);
    }

    @Override
    @Transactional
    @CacheEvict( allEntries = true)
    public int delete(Long id,Long userId) throws Exception {
        try{
            int result = roleDao.delete(id, userId);
            if(result != 1) throw new Exception("删除失败");
            rolePermissionService.deleteByRoleId(userId,id);
            rankRoleService.deleteByRoleId(id);
            userRoleService.deleteByRoleId(id);
            return result;
        }catch(Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @CacheEvict( allEntries = true)
    public int update(Long id,String name, Boolean disabled, String description) {
        return roleDao.update(id,name,disabled,description);
    }

    @Override
    @Cacheable
    public List<SysRoleModel> search(Long id, String name, Boolean disabled,Boolean system, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return roleDao.search(id,name,disabled,system,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    @Cacheable
    public int count(Long id, String name, Boolean disabled,Boolean system, Long userId, Long beginTime, Long endTime, Long ip) {
        return roleDao.count(id,name,disabled,system,userId,beginTime,endTime,ip);
    }

    @Override
    @Cacheable
    public PageModel<SysRoleModel> page(Long id, String name, Boolean disabled,Boolean system, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;

        List<SysRoleModel> data = search(id,name,disabled,system,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(id,name,disabled,system,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }
}
