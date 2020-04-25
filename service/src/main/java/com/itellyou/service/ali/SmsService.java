package com.itellyou.service.ali;

import com.itellyou.model.ali.SmsLogModel;

import java.util.Map;

public interface SmsService {
    SmsLogModel send(String action,String mobile, Map<String,String> data, String ip) throws Exception;
}
