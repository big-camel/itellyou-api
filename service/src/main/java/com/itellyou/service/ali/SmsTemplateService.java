package com.itellyou.service.ali;

import com.itellyou.model.ali.SmsTemplateModel;

import java.util.Map;

public interface SmsTemplateService {

    SmsTemplateModel findById(String id);

    Map<String,SmsTemplateModel> getAll();
}
