package com.itellyou.api.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.itellyou.service.user.UserLoginService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.annotation.MultiRequestBody;
import com.itellyou.api.handler.response.Result;
import com.itellyou.model.ali.SmsLogModel;
import com.itellyou.model.geetest.GeetestResultModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.ali.SmsLogService;
import com.itellyou.service.geetest.GeetestService;
import com.itellyou.service.user.UserLoginLogService;
import com.itellyou.util.CookieUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.validation.Mobile;
import com.itellyou.util.validation.MobileValidator;
import com.itellyou.util.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
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
@RequestMapping("/user/login")
public class LoginController {

    private final UserSearchService userSearchService;
    private final GeetestService geetestService;
    private final UserLoginService loginService;
    private final SmsLogService smsLogService;

    @Autowired
    public LoginController(UserSearchService userSearchService, GeetestService geetestService, UserLoginService loginService, SmsLogService smsLogService){
        this.userSearchService = userSearchService;
        this.geetestService = geetestService;
        this.loginService = loginService;
        this.smsLogService = smsLogService;
    }

    @PostMapping("/account")
    public Result account(HttpServletRequest request, HttpServletResponse response, @MultiRequestBody @NotBlank String username, @MultiRequestBody @NotBlank String password, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel){
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new Result(1001,"Geetest 验证失败");
        }
        UserInfoModel userInfoModel = null;
        if(new MobileValidator().isValid(username,null)){
            userInfoModel = userSearchService.findByMobile(username);
            if(userInfoModel == null){
                return new Result(1002,"未注册的手机号");
            }else if(userInfoModel.isMobileStatus() == false){
                return new Result(10021,"未激活的手机号");
            }

        }else if(new EmailValidator().isValid(username,null)){
            userInfoModel = userSearchService.findByEmail(username);
            if(userInfoModel == null){
                return new Result(1003,"未注册的邮箱");
            }else if(userInfoModel.isMobileStatus() == false){
                return new Result(10031,"未激活的邮箱");
            }
        }else{
            userInfoModel = userSearchService.findByLoginName(username);
            if(userInfoModel == null){
                return new Result(1004,"用户名不存在");
            }
        }
        if(userInfoModel.getLoginPassword().equals(StringUtils.encoderPassword(password))){
            if(userInfoModel.isDisabled()){
                return new Result(1006,"账户已锁定");
            }
            String clientIp = IPUtils.getClientIp(request);
            String token = loginService.createToken(userInfoModel.getId(),IPUtils.toLong(clientIp));
            if(!StringUtils.isNotEmpty(token)){
                return new Result(1007,"登录出错啦");
            }
            loginService.sendToken(response,token);
            return new Result(userInfoModel,"base");
        }
        return new Result(1005,"密码错误");
    }

    @PostMapping("/mobile")
    public Result mobile(HttpServletRequest request, HttpServletResponse response, @MultiRequestBody @NotBlank @Mobile String mobile, @MultiRequestBody @NotBlank String code){

        List<SmsLogModel> listLog = smsLogService.searchByTemplateAndMobile("login",mobile);
        SmsLogModel checkLog = null;
        for(SmsLogModel smsLogModel : listLog){
            Map<String,String> dataMap = JSONObject.parseObject(smsLogModel.getData(),new TypeReference<Map<String,String>>(){});
            if(StringUtils.isNotEmpty(dataMap.get("code")) && dataMap.get("code").equals(code)){
                checkLog = smsLogModel;
                break;
            }
        }
        if(checkLog == null){
            return new Result(1001,"验证码错误或已过期");
        }
        UserInfoModel userInfoModel = userSearchService.findByMobile(mobile);
        if(userInfoModel == null){
            return new Result(1002,"未注册的手机号");
        }
        if(userInfoModel.isDisabled()){
            return new Result(1003,"账户已锁定");
        }
        String clientIp = IPUtils.getClientIp(request);
        String token = loginService.createToken(userInfoModel.getId(),IPUtils.toLong(clientIp));
        if(!StringUtils.isNotEmpty(token)){
            return new Result(1004,"登录出错啦");
        }
        loginService.sendToken(response,token);
        return new Result(userInfoModel,"base");
    }
}
