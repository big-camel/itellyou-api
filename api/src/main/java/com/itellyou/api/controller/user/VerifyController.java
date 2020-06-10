package com.itellyou.api.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.thirdparty.DmLogModel;
import com.itellyou.model.thirdparty.SmsLogModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserVerifyModel;
import com.itellyou.service.thirdparty.DmLogService;
import com.itellyou.service.thirdparty.SmsLogService;
import com.itellyou.service.user.passport.UserVerifyService;
import com.itellyou.util.CookieUtils;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/user/verify")
public class VerifyController {

    private final SmsLogService smsLogService;
    private final DmLogService dmLogService;
    private final UserVerifyService verifyService;

    private final static String VERIFY_COOKIE_KEY = "itellyou_verify";
    private final static int VERIFY_EXPIRED = 10 * 60;

    @Autowired
    public VerifyController( SmsLogService smsLogService,DmLogService dmLogService,UserVerifyService verifyService){
        this.smsLogService = smsLogService;
        this.dmLogService = dmLogService;
        this.verifyService = verifyService;
    }

    @PostMapping("/mobile")
    public ResultModel mobile(UserInfoModel userModel , HttpServletRequest request , HttpServletResponse response , @MultiRequestBody @NotBlank String code){
        if(userModel == null) return new ResultModel(403,"未登陆");
        List<SmsLogModel> listLog = smsLogService.searchByTemplateAndMobile("verify",userModel.getMobile());
        SmsLogModel checkLog = null;
        for(SmsLogModel smsLogModel : listLog){
            Map<String,String> dataMap = JSONObject.parseObject(smsLogModel.getData(),new TypeReference<Map<String,String>>(){});
            if(StringUtils.isNotEmpty(dataMap.get("code")) && dataMap.get("code").equals(code)){
                checkLog = smsLogModel;
                break;
            }
        }
        if(checkLog == null){
            return new ResultModel(1001,"验证码错误或已过期");
        }

        int resultRows = smsLogService.updateStatus(3,checkLog.getId());
        if(resultRows == 0){
            return new ResultModel(0,"更新验证码状态失败");
        }

        String key = StringUtils.createToken(userModel.getId().toString() + userModel.getMobile());
        String ip = IPUtils.getClientIp(request);
        UserVerifyModel verifyModel = new UserVerifyModel(key,false, DateUtils.getTimestamp(),userModel.getId(),IPUtils.toLong(ip));
        int result = verifyService.insert(verifyModel);
        if(result != 1) return new ResultModel(500,"写入验证信息错误");

        CookieUtils.setCookie(response,VERIFY_COOKIE_KEY,key,"/", VERIFY_EXPIRED);

        return new ResultModel(DateUtils.getTimestamp() + VERIFY_EXPIRED);
    }

    @PostMapping("/email")
    public ResultModel email(UserInfoModel userModel , HttpServletRequest request , HttpServletResponse response , @MultiRequestBody @NotBlank String code){
        if(userModel == null) return new ResultModel(403,"未登陆");
        List<DmLogModel> listLog = dmLogService.searchByTemplateAndEmail("verify",userModel.getEmail());
        DmLogModel checkLog = null;
        for(DmLogModel logModel : listLog){
            Map<String,String> dataMap = JSONObject.parseObject(logModel.getData(),new TypeReference<Map<String,String>>(){});
            if(StringUtils.isNotEmpty(dataMap.get("code")) && dataMap.get("code").equals(code)){
                checkLog = logModel;
                break;
            }
        }
        if(checkLog == null){
            return new ResultModel(1001,"验证码错误或已过期");
        }

        int resultRows = dmLogService.updateStatus(3,checkLog.getId());
        if(resultRows == 0){
            return new ResultModel(0,"更新验证码状态失败");
        }

        String key = StringUtils.createToken(userModel.getId().toString() + userModel.getEmail());
        String ip = IPUtils.getClientIp(request);
        UserVerifyModel verifyModel = new UserVerifyModel(key,false, DateUtils.getTimestamp(),userModel.getId(),IPUtils.toLong(ip));
        int result = verifyService.insert(verifyModel);
        if(result != 1) return new ResultModel(500,"写入验证信息错误");

        CookieUtils.setCookie(response,VERIFY_COOKIE_KEY,key,"/", VERIFY_EXPIRED);

        return new ResultModel(DateUtils.getTimestamp() + VERIFY_EXPIRED);
    }
}
