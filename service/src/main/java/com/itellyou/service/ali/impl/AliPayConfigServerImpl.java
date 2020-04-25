package com.itellyou.service.ali.impl;

import com.itellyou.dao.ali.AliPayConfigDao;
import com.itellyou.model.ali.AliPayConfigModel;
import com.itellyou.service.ali.AliPayConfigServer;
import org.springframework.stereotype.Service;

@Service
public class AliPayConfigServerImpl implements AliPayConfigServer {

    private final AliPayConfigDao configDao;

    public AliPayConfigServerImpl(AliPayConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    public AliPayConfigModel getDefault() {
        return configDao.getDefault();
    }
}
