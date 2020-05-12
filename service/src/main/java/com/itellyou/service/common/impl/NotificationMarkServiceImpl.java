package com.itellyou.service.common.impl;

import com.itellyou.dao.common.NotificationMarkDao;
import com.itellyou.model.common.NotificationMarkModel;
import com.itellyou.service.common.NotificationMarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationMarkServiceImpl implements NotificationMarkService {

    private final NotificationMarkDao markDao;

    @Autowired
    public NotificationMarkServiceImpl(NotificationMarkDao markDao){
        this.markDao = markDao;
    }

    @Override
    public int insertOrUpdate(NotificationMarkModel model) {
        return markDao.insertOrUpdate(model);
    }

    @Override
    public NotificationMarkModel findByUserId(Long userId) {
        return markDao.findByUserId(userId);
    }
}
