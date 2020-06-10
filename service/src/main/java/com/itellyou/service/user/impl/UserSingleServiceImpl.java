package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserInfoDao;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.user.UserSingleService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "user_info")
@Service
public class UserSingleServiceImpl implements UserSingleService {

    private final UserInfoDao infoDao;

    public UserSingleServiceImpl(UserInfoDao infoDao) {
        this.infoDao = infoDao;
    }

    @Override
    public UserInfoModel findByName(String name) {
        return infoDao.findByName(name);
    }

    @Override
    public UserInfoModel findByLoginName(String loginName) {
        return infoDao.findByLoginName(loginName);
    }

    @Override
    public UserInfoModel findByMobile(String mobile) {
        return findByMobile(mobile,null);
    }

    @Override
    public UserInfoModel findByMobile(String mobile,Integer mobileStatus) {
        return infoDao.findByMobile(mobile,mobileStatus);
    }

    @Override
    public UserInfoModel findByEmail(String email,Integer emailStatus) {
        return infoDao.findByEmail(email,emailStatus);
    }

    @Override
    public UserInfoModel findByEmail(String email) {
        return findByEmail(email,null);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public UserInfoModel findById(Long id) {
        return infoDao.findById(id);
    }
}
