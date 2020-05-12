package com.itellyou.service.thirdparty;

import com.itellyou.model.thirdparty.SmsLogModel;

import java.util.Map;

public interface SmsService {
    SmsLogModel send(String action,String mobile, Map<String,String> data, String ip) throws Exception;
}
