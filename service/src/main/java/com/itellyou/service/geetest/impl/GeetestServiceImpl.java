package com.itellyou.service.geetest.impl;

import com.alibaba.fastjson.JSON;
import com.itellyou.model.geetest.*;
import com.itellyou.service.geetest.GeetestConfigService;
import com.itellyou.service.geetest.GeetestLogService;
import com.itellyou.service.geetest.GeetestService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.GeetestSdk;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeetestServiceImpl implements GeetestService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final GeetestConfigService geetestConfigService;

    private final GeetestLogService geetestLogService;

    public GeetestServiceImpl(GeetestConfigService geetestConfigService, GeetestLogService geetestLogService) {
        this.geetestConfigService = geetestConfigService;
        this.geetestLogService = geetestLogService;
    }

    public GeetestModel init(String mode, Long userId, GeetestClientTypeEnum clientType, String ip){
        HashMap<String,String> data = new HashMap<>();
        String key = StringUtils.createToken(mode + userId + ip + clientType.getValue());
        data.put("user_id",userId.toString());
        data.put("client_type",clientType.getValue());
        data.put("ip_address",ip);
        GeetestConfigModel configModel = geetestConfigService.getConfig();
        GeetestSdk geetestSdk = new GeetestSdk(configModel.getId(),configModel.getKey(),true);
        geetestSdk.debugCode = false;
        int gtServerStatus = geetestSdk.preProcess(data);
        long ipLong = IPUtils.toLong(ip);
        GeetestLogModel logModel = new GeetestLogModel(key,clientType,ipLong,gtServerStatus,mode,userId,DateUtils.getTimestamp());
        int result = geetestLogService.insert(logModel);
        if(result == 1){
            try {
                String gtString = geetestSdk.getResponseStr();
                Map<String,Object> gtMap = JSON.parseObject(gtString);
                Object objSuccess = gtMap.get("success");
                Integer success = objSuccess == null ? null : (int)objSuccess;
                Object objNewCaptcha = gtMap.get("new_captcha");
                Integer newCaptcha = objNewCaptcha == null ? 1 : (int)objNewCaptcha;
                return new GeetestModel(key,gtMap.get("challenge").toString(),gtMap.get("gt").toString(),success,newCaptcha);
            }catch (Exception exception){
                logger.error(exception.getMessage());
            }
        }
        return null;
    }

    @Override
    public boolean verify(GeetestResultModel geetestResultModel) {

        GeetestConfigModel configModel = geetestConfigService.getConfig();
        if(configModel == null) return false;

        GeetestSdk geetestSdk = new GeetestSdk(configModel.getId(),configModel.getKey(),true);
        geetestSdk.debugCode = false;
        GeetestLogModel geetestLogModel = geetestLogService.findByKey(geetestResultModel.getKey());
        if(geetestLogModel == null) return false;

        HashMap<String,String> data = new HashMap<>();
        data.put("user_id",geetestLogModel.getCreatedUserId().toString());
        data.put("client_type",geetestLogModel.getType().getValue());
        data.put("ip_address", IPUtils.toIpv4(geetestLogModel.getIp()));

        int gtResult = 0;
        if(geetestLogModel.getStatus() == 1){
            try{
                gtResult = geetestSdk.enhencedValidateRequest(geetestResultModel.getChallenge(),geetestResultModel.getValidate(),geetestResultModel.getSeccode(),data);
            }catch (UnsupportedEncodingException exception){
                exception.printStackTrace();
            }
        }else{
            gtResult = geetestSdk.failbackValidateRequest(geetestResultModel.getChallenge(), geetestResultModel.getValidate(), geetestResultModel.getSeccode());
        }
        return gtResult == 1;
    }
}
