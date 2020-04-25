package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserNotificationMarkDao;
import com.itellyou.model.user.UserNotificationMarkModel;
import com.itellyou.service.user.UserNotificationMarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserNotificationMarkServiceImpl implements UserNotificationMarkService {

    private final UserNotificationMarkDao markDao;

    @Autowired
    public UserNotificationMarkServiceImpl(UserNotificationMarkDao markDao){
        this.markDao = markDao;
    }

    @Override
    public int insertOrUpdate(UserNotificationMarkModel model) {
        return markDao.insertOrUpdate(model);
    }

    @Override
    public UserNotificationMarkModel findByUserId(Long userId) {
        return markDao.findByUserId(userId);
    }
}
