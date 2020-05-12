package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserRankRoleDao;
import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.user.UserRankModel;
import com.itellyou.model.user.UserRankRoleModel;
import com.itellyou.service.user.UserRankRoleService;
import com.itellyou.service.user.UserRankService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "user_rank_role")
@Service
public class UserRankRoleServiceImpl implements UserRankRoleService {

    private final UserRankRoleDao rankRoleDao;
    private final UserRankService rankService;

    public UserRankRoleServiceImpl(UserRankRoleDao rankRoleDao, UserRankService rankService) {
        this.rankRoleDao = rankRoleDao;
        this.rankService = rankService;
    }

    @Override
    @CacheEvict(allEntries = true)
    public int insert(UserRankRoleModel model) {
        return rankRoleDao.insert(model);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int delete(Long rankId, Long roleId) {
        return rankRoleDao.delete(rankId,roleId);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int deleteByRoleId(Long roleId) {
        return rankRoleDao.deleteByRoleId(roleId);
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#userId).concat('-rank')")
    public List<SysRoleModel> findRoleByRankId(Long rankId) {
        return rankRoleDao.findRoleByRankId(rankId);
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#userId).concat('-user')")
    public List<SysRoleModel> findRoleByUserId(Long userId) {
        UserRankModel rankModel = rankService.find(userId);
        if(rankModel == null) return null;
        return findRoleByRankId(rankModel.getId());
    }

    @Override
    @Cacheable
    public List<UserRankRoleModel> search(Long roleId, Long rankId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return rankRoleDao.search(roleId,rankId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    @Cacheable
    public int count(Long roleId, Long rankId, Long beginTime, Long endTime, Long ip) {
        return rankRoleDao.count(roleId,rankId,beginTime,endTime,ip);
    }
}
