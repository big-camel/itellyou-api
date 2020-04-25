package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserLoginLogDao;
import com.itellyou.model.user.UserLoginLogModel;
import com.itellyou.service.user.UserLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLoginLogServiceImpl implements UserLoginLogService {

    private final UserLoginLogDao userLoginLogDao;

    @Autowired
    public UserLoginLogServiceImpl(UserLoginLogDao userLoginLogDao){
        this.userLoginLogDao = userLoginLogDao;
    }

    @Override
    public int insert(UserLoginLogModel userLoginLogModel) {
        return userLoginLogDao.insert(userLoginLogModel);
    }

    @Override
    public int setDisabled(Boolean status, String token) {
        return userLoginLogDao.setDisabled(status,token);
    }
}
