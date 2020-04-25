package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserNotificationActorsDao;
import com.itellyou.model.user.UserNotificationActorsModel;
import com.itellyou.service.user.UserNotificationActorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserNotificationActorsServiceImpl implements UserNotificationActorsService {

    private final UserNotificationActorsDao actorsDao;

    @Autowired
    public UserNotificationActorsServiceImpl(UserNotificationActorsDao actorsDao){
        this.actorsDao = actorsDao;
    }

    @Override
    public int insert(UserNotificationActorsModel... models) {
        return actorsDao.insert(models);
    }
}
