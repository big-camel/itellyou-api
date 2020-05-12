package com.itellyou.service.thirdparty.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.itellyou.model.thirdparty.SmsConfigModel;
import com.itellyou.model.thirdparty.SmsLogModel;
import com.itellyou.model.thirdparty.SmsTemplateModel;
import com.itellyou.service.common.ConfigMapService;
import com.itellyou.service.thirdparty.*;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SmsLogService smsLogService;

    private final ConfigMapService<SmsConfigModel> smsConfigService;

    private final ConfigMapService<SmsTemplateModel> smsTemplateService;

    private final AliService aliService;
    @Autowired
    public SmsServiceImpl(SmsLogService smsLogService, SmsConfigServiceImpl smsConfigService, SmsTemplateServiceImpl smsTemplateService, AliService aliService) {
        this.smsLogService = smsLogService;
        this.smsConfigService = smsConfigService;
        this.smsTemplateService = smsTemplateService;
        this.aliService = aliService;
    }

    private String getTimeTemplate(Long seconds,String format){
        return "获取验证码太过频繁，请" + DateUtils.formatDuration(seconds,format) + " 后再试";
    }

    private void checkSendPrivilege(List<SmsLogModel> listSmsLogModel,SmsConfigModel smsConfigModel) throws VerifyCodeException {
        int minuteCount = 0;
        int hourCount = 0;
        Long lastMinuteCreatedTime = 0L;
        Long lastHourCreatedTime = 0L;
        if(listSmsLogModel.size() >= smsConfigModel.getDay()){
            Long seconds = 86400 - (DateUtils.getTimestamp() - listSmsLogModel.get(listSmsLogModel.size() - 1).getCreatedTime());
            String message = getTimeTemplate(seconds,"HH时mm分ss秒");
            throw new VerifyCodeException(seconds,message);
        }
        for(SmsLogModel smsLogModel : listSmsLogModel) {
            if(smsLogModel.getCreatedTime() >= DateUtils.getTimestamp() - 60){
                minuteCount++;
                lastMinuteCreatedTime = smsLogModel.getCreatedTime();
            }else if(smsLogModel.getCreatedTime() >= DateUtils.getTimestamp() - 3600){
                hourCount++;
                lastHourCreatedTime = smsLogModel.getCreatedTime();
            }
        };
        if(hourCount >= smsConfigModel.getHour()){
            Long seconds = 3600 - (DateUtils.getTimestamp() - lastHourCreatedTime);
            String message = getTimeTemplate(seconds,"mm分ss秒");
            throw new VerifyCodeException(seconds,message);
        }
        if(minuteCount >= smsConfigModel.getMinute()){
            Long seconds = 60 - (DateUtils.getTimestamp() - lastMinuteCreatedTime);
            String message = getTimeTemplate(seconds,"ss秒");
            throw new VerifyCodeException(seconds,message);
        }
    }

    /**
     * 根据模版和Ip获取过去一天所发送的日志
     * @param templateId
     * @param ip
     * @param smsConfigModel
     * @throws VerifyCodeException
     */
    private void checkIpPrivilege(String templateId,String ip,SmsConfigModel smsConfigModel) throws VerifyCodeException {

        List<SmsLogModel> listSmsLogModel = smsLogService.searchByTemplateAndIp(templateId,DateUtils.getTimestamp() - 86400,ip);
        if(listSmsLogModel != null && listSmsLogModel.size() > 0 && smsConfigModel != null){
            checkSendPrivilege(listSmsLogModel,smsConfigModel);
        }
    }

    private void checkMobilePrivilege(String templateId,String mobile,SmsConfigModel smsConfigModel) throws VerifyCodeException {

        List<SmsLogModel> listSmsLogModel = smsLogService.searchByTemplateAndMobile(templateId,DateUtils.getTimestamp() - 86400,mobile);
        if(listSmsLogModel != null && listSmsLogModel.size() > 0 && smsConfigModel != null){
            checkSendPrivilege(listSmsLogModel,smsConfigModel);
        }
    }

    public Map<String,String> smsSend(String mobile,String signName,String template,String param){
        IAcsClient client = aliService.getClient();
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", template);
        request.putQueryParameter("TemplateParam", param);
        try {
            CommonResponse response = client.getCommonResponse(request);
            Map<String,String> dataMap = JSON.parseObject(response.getData(),new TypeReference<Map<String, String>>(){});
            return dataMap;
        } catch (ServerException e) {
            e.printStackTrace();
            logger.error(e.getErrMsg());
        } catch (ClientException e) {
            e.printStackTrace();
            logger.error(e.getErrMsg());
        }
        return null;
    }

    @Override
    public SmsLogModel send(String action,String mobile, Map<String, String> data, String ip) throws VerifyCodeException {
        SmsTemplateModel smsTemplateModel = smsTemplateService.find(action);
        if(smsTemplateModel == null) throw new VerifyCodeException("错误的 action，请联系系统管理员");
        Map<String,SmsConfigModel> smsConfigMap = smsConfigService.getMap();
        if(smsConfigMap == null){
            logger.warn("阿里短信接口未对发送次数限制（Ali Sms Config）");
        }else if(!smsConfigMap.containsKey("ip")){
            logger.warn("阿里短信接口未对IP发送次数限制（Ali Sms Config）");
        }
        else if(!smsConfigMap.containsKey("mobile")){
            logger.warn("阿里短信接口未对第三方接口发送次数限制（Ali Sms Config）");
        }

        if(smsConfigMap != null && smsConfigMap.containsKey("ip")){
            checkIpPrivilege(smsTemplateModel.getId(),ip,smsConfigMap.get("ip"));
        }
        if(smsConfigMap != null && smsConfigMap.containsKey("mobile")){
            checkMobilePrivilege(smsTemplateModel.getId(),mobile,smsConfigMap.get("mobile"));
        }
        if(!data.containsKey("expire")){
            data.put("expire",String.valueOf(smsTemplateModel.getExpire() / 60));
        }
        String param = smsTemplateModel.getParam();
        for(Map.Entry<String, String> entry : data.entrySet()){
            String dataKey = entry.getKey();
            String dataValue = entry.getValue();
            param = StringUtils.replace(param,"${" + dataKey + "}",dataValue);
        }
        logger.info("mobile:{},data:{}",mobile, param);
        Long ipLong = IPUtils.toLong(ip);
        SmsLogModel smsLogModel = new SmsLogModel(null,mobile,smsTemplateModel.getId(),param,0,DateUtils.getTimestamp(),ipLong);
        if(smsLogService.insert(smsLogModel) == 0)
            throw new VerifyCodeException("写入日志出错啦");

        Map<String,String> resultMap = smsSend(mobile,smsTemplateModel.getSignName(),smsTemplateModel.getCode(),param);
        if(resultMap == null || !resultMap.get("Code").equals("OK")){
            if(resultMap != null){
                logger.error(resultMap.get("Message").toString());
            }
            smsLogService.updateStatus(2,smsLogModel.getId());
            return null;
        }
        if(smsLogService.updateStatus(1,smsLogModel.getId()) == 1)
            return smsLogModel;
        throw new VerifyCodeException("更新日志状态失败");
    }
}
