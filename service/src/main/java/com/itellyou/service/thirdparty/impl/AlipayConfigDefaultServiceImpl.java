package com.itellyou.service.thirdparty.impl;

import com.itellyou.dao.thirdparty.AliPayConfigDao;
import com.itellyou.model.thirdparty.AliPayConfigModel;
import com.itellyou.service.common.ConfigDefaultService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "alipay_config")
@Service
public class AlipayConfigDefaultServiceImpl implements ConfigDefaultService<AliPayConfigModel> {

    private final AliPayConfigDao configDao;

    public AlipayConfigDefaultServiceImpl(AliPayConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    @Cacheable(key = "#root.methodName",unless = "#result == null")
    public AliPayConfigModel getDefault() {
        return configDao.getDefault();
    }
}
