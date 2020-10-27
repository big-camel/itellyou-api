package com.itellyou.service.user.rank.impl;

import com.itellyou.dao.user.UserRankRoleDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.user.UserRankModel;
import com.itellyou.model.user.UserRankRoleModel;
import com.itellyou.service.user.rank.UserRankRoleService;
import com.itellyou.service.user.rank.UserRankSingleService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = CacheKeys.USER_RANK_ROLE_KEY)
@Service
public class UserRankRoleServiceImpl implements UserRankRoleService {

    private final UserRankRoleDao rankRoleDao;
    private final UserRankSingleService rankSingleService;

    public UserRankRoleServiceImpl(UserRankRoleDao rankRoleDao, UserRankSingleService rankSingleService) {
        this.rankRoleDao = rankRoleDao;
        this.rankSingleService = rankSingleService;
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
        UserRankModel rankModel = rankSingleService.find(userId);
        if(rankModel == null) return null;
        return findRoleByRankId(rankModel.getId());
    }

    @Override
    @Cacheable(unless = "#result == null")
    public List<UserRankRoleModel> search(Long roleId, Long rankId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return rankRoleDao.search(roleId,rankId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public int count(Long roleId, Long rankId, Long beginTime, Long endTime, Long ip) {
        return rankRoleDao.count(roleId,rankId,beginTime,endTime,ip);
    }
}
