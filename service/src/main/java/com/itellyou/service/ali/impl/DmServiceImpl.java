package com.itellyou.service.ali.impl;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.itellyou.model.ali.DmConfigModel;
import com.itellyou.model.ali.DmLogModel;
import com.itellyou.model.ali.DmTemplateModel;
import com.itellyou.service.ali.*;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DmServiceImpl implements DmService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DmLogService dmLogService;

    private final DmConfigService dmConfigService;

    private final DmTemplateService dmTemplateService;

    private final AliService aliService;

    @Autowired
    public DmServiceImpl(DmLogService dmLogService, DmConfigService dmConfigService, DmTemplateService dmTemplateService, AliService aliService) {
        this.dmLogService = dmLogService;
        this.dmConfigService = dmConfigService;
        this.dmTemplateService = dmTemplateService;
        this.aliService = aliService;
    }

    private String getTimeTemplate(Long seconds,String format){
        return "获取验证码太过频繁，请" + DateUtils.formatDuration(seconds,format) + " 后再试";
    }

    private void checkSendPrivilege(List<DmLogModel> listDmLogModel,DmConfigModel dmConfigModel) throws VerifyCodeException {
        int minuteCount = 0;
        int hourCount = 0;
        Long lastMinuteCreatedTime = 0L;
        Long lastHourCreatedTime = 0L;
        if(listDmLogModel.size() >= dmConfigModel.getDay()){
            Long seconds = 86400 - (DateUtils.getTimestamp() - listDmLogModel.get(listDmLogModel.size() - 1).getCreatedTime());
            String message = getTimeTemplate(seconds,"HH时mm分ss秒");
            throw new VerifyCodeException(seconds,message);
        }
        for(DmLogModel dmLogModel : listDmLogModel) {
            if(dmLogModel.getCreatedTime() >= DateUtils.getTimestamp() - 60){
                minuteCount++;
                lastMinuteCreatedTime = dmLogModel.getCreatedTime();
            }else if(dmLogModel.getCreatedTime() >= DateUtils.getTimestamp() - 3600){
                hourCount++;
                lastHourCreatedTime = dmLogModel.getCreatedTime();
            }
        };
        if(hourCount >= dmConfigModel.getHour()){
            Long seconds = 3600 - (DateUtils.getTimestamp() - lastHourCreatedTime);
            String message = getTimeTemplate(seconds,"mm分ss秒");
            throw new VerifyCodeException(seconds,message);
        }
        if(minuteCount >= dmConfigModel.getMinute()){
            Long seconds = 60 - (DateUtils.getTimestamp() - lastMinuteCreatedTime);
            String message = getTimeTemplate(seconds,"ss秒");
            throw new VerifyCodeException(seconds,message);
        }
    }

    /**
     * 根据模版和Ip获取过去一天所发送的日志
     * @param templateId
     * @param ip
     * @param dmConfigModel
     * @throws VerifyCodeException
     */
    private void checkIpPrivilege(String templateId,String ip,DmConfigModel dmConfigModel) throws VerifyCodeException {

        List<DmLogModel> listDmLogModel = dmLogService.searchByTemplateAndIp(templateId,DateUtils.getTimestamp() - 86400,ip);
        if(listDmLogModel != null && listDmLogModel.size() > 0 && dmConfigModel != null){
            checkSendPrivilege(listDmLogModel,dmConfigModel);
        }
    }

    private void checkEmailPrivilege(String templateId,String email,DmConfigModel dmConfigModel) throws VerifyCodeException {

        List<DmLogModel> listDmLogModel = dmLogService.searchByTemplateAndEmail(templateId,DateUtils.getTimestamp() - 86400,email);
        if(listDmLogModel != null && listDmLogModel.size() > 0 && dmConfigModel != null){
            checkSendPrivilege(listDmLogModel,dmConfigModel);
        }
    }

    public String dmSend(String email,String account , String sendName ,String title , String tagName ,String body){
        IAcsClient client = aliService.getClient();
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            request.setAccountName(account);
            request.setAddressType(1);
            request.setReplyToAddress(true);
            request.setToAddress(email);
            request.setSubject(title);
            if(StringUtils.isNotEmpty(tagName)){
                request.setTagName(tagName);
            }
            request.setHtmlBody(body);
            if(StringUtils.isNotEmpty(sendName)){
                request.setFromAlias(sendName);
            }
            SingleSendMailResponse response = client.getAcsResponse(request);
            return response.getEnvId();
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
    public DmLogModel send(String action,String email, Map<String, String> data, String ip) throws VerifyCodeException {
        DmTemplateModel dmTemplateModel = dmTemplateService.findById(action);
        if(dmTemplateModel == null) throw new VerifyCodeException("错误的 action，请联系系统管理员");
        Map<String,DmConfigModel> dmConfigMap = dmConfigService.getAll();
        if(dmConfigMap == null){
            logger.warn("阿里邮箱接口未对发送次数限制（Ali Dm Config）");
        }else if(!dmConfigMap.containsKey("ip")){
            logger.warn("阿里邮箱接口未对IP发送次数限制（Ali Dm Config）");
        }
        else if(!dmConfigMap.containsKey("email")){
            logger.warn("阿里邮箱接口未对第三方接口发送次数限制（Ali Dm Config）");
        }

        if(dmConfigMap != null && dmConfigMap.containsKey("ip")){
            checkIpPrivilege(dmTemplateModel.getId(),ip,dmConfigMap.get("ip"));
        }
        if(dmConfigMap != null && dmConfigMap.containsKey("email")){
            checkEmailPrivilege(dmTemplateModel.getId(),email,dmConfigMap.get("email"));
        }
        if(!data.containsKey("expire")){
            data.put("expire",String.valueOf(dmTemplateModel.getExpire() / 60));
        }

        String param = dmTemplateModel.getParam();
        String body = dmTemplateModel.getBody();
        for(Map.Entry<String, String> entry : data.entrySet()){
            String dataKey = entry.getKey();
            String dataValue = entry.getValue();
            param = StringUtils.replace(param,"${" + dataKey + "}",dataValue);
            body = StringUtils.replace(body,"${" + dataKey + "}",dataValue);
        }
        dmTemplateModel.setBody(body);
        logger.info("email:" + email + ",data:" + param);
        Long ipLong = IPUtils.toLong(ip);
        DmLogModel dmLogModel = new DmLogModel(null,email,dmTemplateModel.getId(),param,0,DateUtils.getTimestamp(),ipLong);
        if(dmLogService.insert(dmLogModel) == 0)
            throw new VerifyCodeException("写入日志出错啦");

        String envId = dmSend(email,dmTemplateModel.getSendAddr(),dmTemplateModel.getSendName(),dmTemplateModel.getTitle(),dmTemplateModel.getTagName(),dmTemplateModel.getBody());
        if(envId == null){
            dmLogService.updateStatus(2,dmLogModel.getId());
            return null;
        }
        if(dmLogService.updateStatus(1,dmLogModel.getId()) == 1)
            return dmLogModel;
        throw new VerifyCodeException("更新日志状态失败");
    }
}
