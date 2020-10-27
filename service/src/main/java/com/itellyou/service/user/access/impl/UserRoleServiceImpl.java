package com.itellyou.service.user.access.impl;

import com.itellyou.dao.user.UserRoleDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.user.UserRoleModel;
import com.itellyou.service.user.rank.UserRankRoleService;
import com.itellyou.service.user.access.UserRoleService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = CacheKeys.USER_ROLE_KEY)
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleDao userRoleDao;
    private final UserRankRoleService rankRoleService;

    public UserRoleServiceImpl(UserRoleDao userRoleDao, UserRankRoleService rankRoleService) {
        this.userRoleDao = userRoleDao;
        this.rankRoleService = rankRoleService;
    }

    @Override
    @CacheEvict(allEntries = true)
    public int insert(UserRoleModel model) {
        return userRoleDao.insert(model);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int delete(Long userId, Long roleId) {
        return userRoleDao.delete(userId,roleId);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int deleteByRoleId(Long roleId) {
        return userRoleDao.deleteByRoleId(roleId);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public List<SysRoleModel> findRoleByUserId(Long userId,boolean includeRank) {
        List<SysRoleModel> userRoles = userRoleDao.findRoleByUserId(userId);
        if(includeRank) {
            List<SysRoleModel> rankRoles = rankRoleService.findRoleByUserId(userId);
            if(rankRoles != null)
                userRoles.addAll(rankRoles);
        }
        return userRoles;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public List<SysRoleModel> findRoleByUserId(Long userId) {
        return findRoleByUserId(userId,false);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public List<UserRoleModel> search(Long roleId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return userRoleDao.search(roleId,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public int count(Long roleId, Long userId, Long beginTime, Long endTime, Long ip) {
        return userRoleDao.count(roleId,userId,beginTime,endTime,ip);
    }

    @Override
    public boolean isRoot(Long userId) {
        List<SysRoleModel> listRole = findRoleByUserId(userId);
        return listRole.stream().filter(role -> role.getId().equals(3l)).findFirst().isPresent();
    }
}
