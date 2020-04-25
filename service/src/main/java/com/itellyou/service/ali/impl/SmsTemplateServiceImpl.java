package com.itellyou.service.ali.impl;

import com.itellyou.dao.ali.SmsTemplateDao;
import com.itellyou.model.ali.SmsTemplateModel;
import com.itellyou.service.ali.SmsTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SmsTemplateServiceImpl implements SmsTemplateService {

    @Autowired
    private SmsTemplateDao smsTemplateDao;

    @Override
    public SmsTemplateModel findById(String id) {
        return smsTemplateDao.findById(id);
    }

    @Override
    public Map<String, SmsTemplateModel> getAll() {
        return smsTemplateDao.getAll();
    }
}
