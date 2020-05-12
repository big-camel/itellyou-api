package com.itellyou.service.thirdparty.impl;

import com.itellyou.dao.thirdparty.SmsConfigDao;
import com.itellyou.model.thirdparty.SmsConfigModel;
import com.itellyou.service.common.ConfigMapService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@CacheConfig(cacheNames = "ali_sms_config")
@Service
public class SmsConfigServiceImpl implements ConfigMapService<SmsConfigModel> {

    private final SmsConfigDao smsConfigDao;

    public SmsConfigServiceImpl(SmsConfigDao smsConfigDao) {
        this.smsConfigDao = smsConfigDao;
    }

    @Override
    @Cacheable
    public SmsConfigModel find(String type) {
        return smsConfigDao.get(type);
    }

    @Override
    @Cacheable(key = "#root.methodName")
    public Map<String, SmsConfigModel> getMap() {
        return smsConfigDao.getAll();
    }
}
