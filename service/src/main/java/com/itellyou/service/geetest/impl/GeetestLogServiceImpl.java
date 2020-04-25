package com.itellyou.service.geetest.impl;

import com.itellyou.dao.geetest.GeetestLogDao;
import com.itellyou.model.geetest.GeetestLogModel;
import com.itellyou.service.geetest.GeetestLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeetestLogServiceImpl implements GeetestLogService {

    @Autowired
    private GeetestLogDao geetestLogDao;

    @Override
    public int insert(GeetestLogModel geetestLogModel) {
        return geetestLogDao.insert(geetestLogModel);
    }

    @Override
    public GeetestLogModel findByKey(String key) {
        return geetestLogDao.findByKey(key);
    }
}
