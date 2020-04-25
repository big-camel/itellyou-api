package com.itellyou.service.ali;

import com.itellyou.model.ali.SmsConfigModel;

import java.util.Map;

public interface SmsConfigService {

    SmsConfigModel get(String type);

    Map<String,SmsConfigModel> getAll();
}
