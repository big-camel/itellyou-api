package com.itellyou.service.geetest.impl;

import com.itellyou.dao.geetest.GeetestConfigDao;
import com.itellyou.model.geetest.GeetestConfigModel;
import com.itellyou.service.geetest.GeetestConfigService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeetestConfigServiceImpl implements GeetestConfigService {

    private static final Log log = LogFactory.getLog(GeetestConfigServiceImpl.class);

    @Autowired
    private GeetestConfigDao geetestConfigDao;

    @Override
    public GeetestConfigModel getConfig() {
        return geetestConfigDao.getConfig();
    }
}
