package com.itellyou.service.common.impl;

import com.itellyou.dao.common.NotificationActorsDao;
import com.itellyou.model.common.NotificationActorsModel;
import com.itellyou.service.common.NotificationActorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationActorsServiceImpl implements NotificationActorsService {

    private final NotificationActorsDao actorsDao;

    @Autowired
    public NotificationActorsServiceImpl(NotificationActorsDao actorsDao){
        this.actorsDao = actorsDao;
    }

    @Override
    public int insert(NotificationActorsModel... models) {
        return actorsDao.insert(models);
    }
}
