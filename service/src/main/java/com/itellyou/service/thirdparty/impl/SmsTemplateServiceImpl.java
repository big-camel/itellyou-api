package com.itellyou.service.thirdparty.impl;

import com.itellyou.dao.thirdparty.SmsTemplateDao;
import com.itellyou.model.thirdparty.SmsTemplateModel;
import com.itellyou.service.common.ConfigMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SmsTemplateServiceImpl implements ConfigMapService<SmsTemplateModel> {

    @Autowired
    private SmsTemplateDao smsTemplateDao;

    @Override
    public SmsTemplateModel find(String id) {
        return smsTemplateDao.findById(id);
    }

    @Override
    public Map<String, SmsTemplateModel> getMap() {
        return smsTemplateDao.getAll();
    }
}
