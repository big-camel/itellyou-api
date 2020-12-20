package com.itellyou.api.controller;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.thirdparty.DmLogModel;
import com.itellyou.model.thirdparty.GeetestResultModel;
import com.itellyou.model.thirdparty.SmsLogModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.thirdparty.DmService;
import com.itellyou.service.thirdparty.GeetestService;
import com.itellyou.service.thirdparty.SmsService;
import com.itellyou.service.thirdparty.VerifyCodeException;
import com.itellyou.service.user.UserSingleService;
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
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/validation")
public class ValidationController {
    private final SmsService smsService;
    private final DmService dmService;
    private final GeetestService geetestService;
    private final UserSingleService userSearchService;

    @Autowired
    public ValidationController (SmsService smsService,DmService dmService,GeetestService geetestService,UserSingleService userSearchService){
        this.smsService = smsService;
        this.dmService = dmService;
        this.geetestService = geetestService;
        this.userSearchService = userSearchService;
    }

    private ResultModel sendMobileCode(String action, String mobile, String clientIp){
        Map<String,Object> dataResult = new HashMap<>();
        dataResult.put("mobile",mobile);
        try{
            Map<String,String > sendData = new HashMap<>();
            String code = StringUtils.randomString(6, StringUtils.RandomType.NUMBER);
            sendData.put("code",code);

            SmsLogModel smsLogModel = smsService.send(action,mobile,sendData,clientIp);
            if(smsLogModel == null){
                return new ResultModel(1003,"发送短信出错了",dataResult);
            }
            dataResult.put("time", DateUtils.getTimestamp(smsLogModel.getCreatedTime()));
        }catch (VerifyCodeException e){
            e.printStackTrace();
            Long seconds = e.getSeconds();
            if(seconds != null){
                dataResult.put("seconds",seconds);
            }
            return new ResultModel(1004,e.getMessage(),dataResult);
        }catch (Exception e){
            e.printStackTrace();
            return new ResultModel(1005,e.getMessage(),dataResult);
        }
        return new ResultModel(dataResult);
    }

    private ResultModel sendEmailCode(String action, String email, String clientIp){
        Map<String,Object> dataResult = new HashMap<>();
        dataResult.put("email",email);
        try{
            Map<String,String > sendData = new HashMap<>();
            String code = StringUtils.randomString(6, StringUtils.RandomType.NUMBER);
            sendData.put("code",code);

            DmLogModel logModel = dmService.send(action,email,sendData,clientIp);
            if(logModel == null){
                return new ResultModel(1003,"发送邮件出错了",dataResult);
            }
            dataResult.put("time",DateUtils.getTimestamp(logModel.getCreatedTime()));
        }catch (VerifyCodeException e){
            e.printStackTrace();
            Long seconds = e.getSeconds();
            if(seconds != null){
                dataResult.put("seconds",seconds);
            }
            return new ResultModel(1004,e.getMessage(),dataResult);
        }catch (Exception e){
            e.printStackTrace();
            return new ResultModel(1005,e.getMessage(),dataResult);
        }
        return new ResultModel(dataResult);
    }

    @PostMapping("/register/code")
    public ResultModel register(HttpServletRequest request, @MultiRequestBody @NotBlank String mobile, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel) {
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new ResultModel(1001,"Geetest 验证失败");
        }

        UserInfoModel userInfoModel = userSearchService.findByMobile(mobile);
        if(userInfoModel != null){
            return new ResultModel(1002,"亲，手机号已被注册了",mobile);
        }

        String clientIp = IPUtils.getClientIp(request);
        return sendMobileCode("register",mobile,clientIp);
    }

    @PostMapping("/login/code")
    public ResultModel login(HttpServletRequest request, @MultiRequestBody @NotBlank String mobile, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel) {
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new ResultModel(1001,"Geetest 验证失败");
        }

        UserInfoModel userInfoModel = userSearchService.findByMobile(mobile);
        if(userInfoModel == null){
            return new ResultModel(1002,"亲，手机号还未注册",mobile);
        }

        String clientIp = IPUtils.getClientIp(request);
        return sendMobileCode("login",mobile,clientIp);
    }

    @PostMapping("/verify/mobile/code")
    public ResultModel verifyMobile(UserInfoModel userModel, HttpServletRequest request, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel) {
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new ResultModel(1001,"Geetest 验证失败");
        }

        if(userModel == null) return new ResultModel(403,"未登陆");
        if(userModel.isDisabled())  return new ResultModel(403,"用户状态不正确");
        if(!userModel.isMobileStatus()) return new ResultModel(1002,"手机号还未验证或无效");
        String mobile = userModel.getMobile();
        String clientIp = IPUtils.getClientIp(request);
        return sendMobileCode("verify",mobile,clientIp);
    }

    @PostMapping("/verify/email/code")
    public ResultModel verifyEmail(UserInfoModel userModel, HttpServletRequest request, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel) {
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new ResultModel(1001,"Geetest 验证失败");
        }

        if(userModel == null) return new ResultModel(403,"未登陆");
        if(userModel.isDisabled())  return new ResultModel(403,"用户状态不正确");
        if(!userModel.isEmailStatus()) return new ResultModel(1002,"邮箱还未验证或无效");
        String clientIp = IPUtils.getClientIp(request);
        return sendEmailCode("verify",userModel.getEmail(),clientIp);
    }

    @PostMapping("/replace/mobile/code")
    public ResultModel replaceMobile(UserInfoModel userModel, HttpServletRequest request, @MultiRequestBody @NotBlank String mobile, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel) {
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new ResultModel(1001,"Geetest 验证失败");
        }
        if(userModel == null) return new ResultModel(403,"未登陆");
        if(userModel.isDisabled())  return new ResultModel(403,"用户状态不正确");

        UserInfoModel userInfoModel = userSearchService.findByMobile(mobile);
        if(userInfoModel != null || userModel.getMobile() == mobile){
            return new ResultModel(1002,"手机号已被占用",mobile);
        }

        String clientIp = IPUtils.getClientIp(request);
        return sendMobileCode("replace",mobile,clientIp);
    }

    @PostMapping("/replace/email/code")
    public ResultModel replaceEmail(UserInfoModel userModel, HttpServletRequest request, @MultiRequestBody @NotBlank String email, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel) {
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new ResultModel(1001,"Geetest 验证失败");
        }
        if(userModel == null) return new ResultModel(403,"未登陆");
        if(userModel.isDisabled())  return new ResultModel(403,"用户状态不正确");

        UserInfoModel emailUser = userSearchService.findByEmail(email);
        if(emailUser != null || userModel.getEmail() == email){
            return new ResultModel(1002,"邮箱已被占用",email);
        }

        String clientIp = IPUtils.getClientIp(request);
        return sendEmailCode("replace",email,clientIp);
    }

    @PostMapping("/login/oauth/code")
    public ResultModel loginOauth(HttpServletRequest request, @MultiRequestBody @NotBlank String mobile, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel) {
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new ResultModel(1001,"Geetest 验证失败");
        }

        String clientIp = IPUtils.getClientIp(request);
        return sendMobileCode("verify",mobile,clientIp);
    }
}
