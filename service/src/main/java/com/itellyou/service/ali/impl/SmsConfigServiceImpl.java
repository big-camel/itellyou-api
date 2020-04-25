package com.itellyou.service.ali.impl;

import com.itellyou.dao.ali.SmsConfigDao;
import com.itellyou.model.ali.SmsConfigModel;
import com.itellyou.service.ali.SmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SmsConfigServiceImpl implements SmsConfigService {

    @Autowired
    private SmsConfigDao smsConfigDao;

    @Override
    public SmsConfigModel get(String type) {
        return smsConfigDao.get(type);
    }

    @Override
    public Map<String, SmsConfigModel> getAll() {
        return smsConfigDao.getAll();
    }
}
