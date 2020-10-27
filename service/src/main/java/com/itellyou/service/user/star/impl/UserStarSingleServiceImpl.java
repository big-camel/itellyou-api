package com.itellyou.service.user.star.impl;

import com.itellyou.dao.user.UserStarDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.user.UserStarModel;
import com.itellyou.service.user.star.UserStarSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = CacheKeys.USER_STAR_KEY)
@Service
public class UserStarSingleServiceImpl implements UserStarSingleService {

    private final UserStarDao starDao;

    public UserStarSingleServiceImpl(UserStarDao starDao) {
        this.starDao = starDao;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#userId).concat('-').concat(#followerId)",unless = "#result == null")
    public UserStarModel find(Long userId, Long followerId) {
        List<UserStarModel> starModels = starDao.search(userId != null ? new HashSet<Long>(){{add(userId);}} : null,followerId,null,null,null,null,null,null);
        return starModels != null && starModels.size() > 0 ? starModels.get(0) : null;
    }

    @Override
    public List<UserStarModel> search(Collection<Long> userIds, Long followerId) {
        return RedisUtils.fetch(CacheKeys.USER_STAR_KEY, UserStarModel.class,userIds,(Collection<Long> fetchIds) ->
                starDao.search(fetchIds,followerId,null,null,null,null,null,null),
                id -> id + "-" + followerId,
                UserStarModel::cacheKey);
    }
}
