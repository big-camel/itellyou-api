package com.itellyou.service.ali.impl;

import com.itellyou.dao.ali.AliPayConfigDao;
import com.itellyou.model.ali.AliPayConfigModel;
import com.itellyou.service.ali.AlipayConfigService;
import org.springframework.stereotype.Service;

@Service
public class AlipayConfigServiceImpl implements AlipayConfigService {

    private final AliPayConfigDao configDao;

    public AlipayConfigServiceImpl(AliPayConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    public AliPayConfigModel getDefault() {
        return configDao.getDefault();
    }
}
