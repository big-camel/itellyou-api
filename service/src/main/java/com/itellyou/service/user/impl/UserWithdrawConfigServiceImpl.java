package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserWithdrawConfigDao;
import com.itellyou.model.user.UserWithdrawConfigModel;
import com.itellyou.service.user.UserWithdrawConfigService;
import org.springframework.stereotype.Service;

@Service
public class UserWithdrawConfigServiceImpl implements UserWithdrawConfigService {

    private final UserWithdrawConfigDao configDao;

    public UserWithdrawConfigServiceImpl(UserWithdrawConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    public UserWithdrawConfigModel getDefault() {
        return configDao.getDefault();
    }
}
