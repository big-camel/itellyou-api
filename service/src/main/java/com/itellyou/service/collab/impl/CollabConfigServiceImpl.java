package com.itellyou.service.collab.impl;

import com.itellyou.dao.collab.CollabConfigDao;
import com.itellyou.model.collab.CollabConfigModel;
import com.itellyou.service.collab.CollabConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollabConfigServiceImpl implements CollabConfigService {
    @Autowired
    private CollabConfigDao collabConfigDao;
    @Override
    public CollabConfigModel findByKey(String key) {
        return collabConfigDao.findByKey(key);
    }
}
