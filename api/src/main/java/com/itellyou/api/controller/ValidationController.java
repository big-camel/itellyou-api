package com.itellyou.api.controller;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.ali.DmLogModel;
import com.itellyou.model.ali.SmsLogModel;
import com.itellyou.model.geetest.GeetestResultModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.ali.DmService;
import com.itellyou.service.ali.SmsService;
import com.itellyou.service.ali.VerifyCodeException;
import com.itellyou.service.geetest.GeetestService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    private final UserSearchService userSearchService;

    @Autowired
    public ValidationController (SmsService smsService,DmService dmService,GeetestService geetestService,UserSearchService userSearchService){
        this.smsService = smsService;
        this.dmService = dmService;
        this.geetestService = geetestService;
        this.userSearchService = userSearchService;
    }

    private Result sendMobileCode(String action,String mobile,String clientIp){
        Map<String,Object> dataResult = new HashMap<>();
        dataResult.put("mobile",mobile);
        try{
            Map<String,String > sendData = new HashMap<>();
            String code = StringUtils.randomString(6, StringUtils.RandomType.NUMBER);
            sendData.put("code",code);

            SmsLogModel smsLogModel = smsService.send(action,mobile,sendData,clientIp);
            if(smsLogModel == null){
                return new Result(1003,"发送短信出错了",dataResult);
            }
            dataResult.put("time",smsLogModel.getCreatedTime());
        }catch (VerifyCodeException e){
            e.printStackTrace();
            Long seconds = e.getSeconds();
            if(seconds != null){
                dataResult.put("seconds",seconds);
            }
            return new Result(1004,e.getMessage(),dataResult);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(1005,e.getMessage(),dataResult);
        }
        return new Result(dataResult);
    }

    private Result sendEmailCode(String action,String email,String clientIp){
        Map<String,Object> dataResult = new HashMap<>();
        dataResult.put("email",email);
        try{
            Map<String,String > sendData = new HashMap<>();
            String code = StringUtils.randomString(6, StringUtils.RandomType.NUMBER);
            sendData.put("code",code);

            DmLogModel logModel = dmService.send(action,email,sendData,clientIp);
            if(logModel == null){
                return new Result(1003,"发送邮件出错了",dataResult);
            }
            dataResult.put("time",logModel.getCreatedTime());
        }catch (VerifyCodeException e){
            e.printStackTrace();
            Long seconds = e.getSeconds();
            if(seconds != null){
                dataResult.put("seconds",seconds);
            }
            return new Result(1004,e.getMessage(),dataResult);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(1005,e.getMessage(),dataResult);
        }
        return new Result(dataResult);
    }

    @PostMapping("/register/code")
    public Result register(HttpServletRequest request, @MultiRequestBody @NotBlank String mobile, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel) {
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new Result(1001,"Geetest 验证失败");
        }

        UserInfoModel userInfoModel = userSearchService.findByMobile(mobile);
        if(userInfoModel != null){
            return new Result(1002,"亲，手机号已被注册了",mobile);
        }

        String clientIp = IPUtils.getClientIp(request);
        return sendMobileCode("register",mobile,clientIp);
    }

    @PostMapping("/login/code")
    public Result login(HttpServletRequest request, @MultiRequestBody @NotBlank String mobile, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel) {
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new Result(1001,"Geetest 验证失败");
        }

        UserInfoModel userInfoModel = userSearchService.findByMobile(mobile);
        if(userInfoModel == null){
            return new Result(1002,"亲，手机号还未注册",mobile);
        }

        String clientIp = IPUtils.getClientIp(request);
        return sendMobileCode("login",mobile,clientIp);
    }

    @PostMapping("/verify/mobile/code")
    public Result verifyMobile(UserInfoModel userModel,HttpServletRequest request, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel) {
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new Result(1001,"Geetest 验证失败");
        }

        if(userModel == null) return new Result(403,"未登陆");
        if(userModel.isDisabled())  return new Result(403,"用户状态不正确");
        if(!userModel.isMobileStatus()) return new Result(1002,"手机号还未验证或无效");
        String mobile = userModel.getMobile();
        String clientIp = IPUtils.getClientIp(request);
        return sendMobileCode("verify",mobile,clientIp);
    }

    @PostMapping("/verify/email/code")
    public Result verifyEmail(UserInfoModel userModel,HttpServletRequest request, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel) {
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new Result(1001,"Geetest 验证失败");
        }

        if(userModel == null) return new Result(403,"未登陆");
        if(userModel.isDisabled())  return new Result(403,"用户状态不正确");
        if(!userModel.isEmailStatus()) return new Result(1002,"邮箱还未验证或无效");
        String clientIp = IPUtils.getClientIp(request);
        return sendEmailCode("verify",userModel.getEmail(),clientIp);
    }

    @PostMapping("/replace/mobile/code")
    public Result replaceMobile(UserInfoModel userModel,HttpServletRequest request, @MultiRequestBody @NotBlank String mobile, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel) {
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new Result(1001,"Geetest 验证失败");
        }
        if(userModel == null) return new Result(403,"未登陆");
        if(userModel.isDisabled())  return new Result(403,"用户状态不正确");

        UserInfoModel userInfoModel = userSearchService.findByMobile(mobile);
        if(userInfoModel != null || userModel.getMobile() == mobile){
            return new Result(1002,"手机号已被占用",mobile);
        }

        String clientIp = IPUtils.getClientIp(request);
        return sendMobileCode("replace",mobile,clientIp);
    }

    @PostMapping("/replace/email/code")
    public Result replaceEmail(UserInfoModel userModel,HttpServletRequest request, @MultiRequestBody @NotBlank String email, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel) {
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new Result(1001,"Geetest 验证失败");
        }
        if(userModel == null) return new Result(403,"未登陆");
        if(userModel.isDisabled())  return new Result(403,"用户状态不正确");

        UserInfoModel emailUser = userSearchService.findByEmail(email);
        if(emailUser != null || userModel.getEmail() == email){
            return new Result(1002,"邮箱已被占用",email);
        }

        String clientIp = IPUtils.getClientIp(request);
        return sendEmailCode("replace",email,clientIp);
    }

    @PostMapping("/login/oauth/code")
    public Result loginOauth(HttpServletRequest request, @MultiRequestBody @NotBlank String mobile, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel) {
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new Result(1001,"Geetest 验证失败");
        }

        String clientIp = IPUtils.getClientIp(request);
        return sendMobileCode("verify",mobile,clientIp);
    }
}
