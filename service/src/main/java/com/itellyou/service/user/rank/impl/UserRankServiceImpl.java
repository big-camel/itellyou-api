package com.itellyou.service.user.rank.impl;

import com.itellyou.dao.user.UserRankDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.user.UserRankModel;
import com.itellyou.service.user.rank.UserRankService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = CacheKeys.USER_RANK_KEY)
@Service
public class UserRankServiceImpl implements UserRankService {

    private final UserRankDao rankDao;

    public UserRankServiceImpl(UserRankDao rankDao) {
        this.rankDao = rankDao;
    }

    @Override
    @CacheEvict(allEntries = true)
    public int insert(UserRankModel model) {
        return rankDao.insert(model);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int deleteById(Long id) {
        return rankDao.deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int update(UserRankModel model) {
        return rankDao.update(model);
    }
}
