package com.itellyou.api.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.SysPermissionModel;
import com.itellyou.model.sys.SysPermissionPlatform;
import com.itellyou.model.thirdparty.GeetestResultModel;
import com.itellyou.model.thirdparty.SmsLogModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.sys.SysPermissionService;
import com.itellyou.service.thirdparty.GeetestService;
import com.itellyou.service.thirdparty.SmsLogService;
import com.itellyou.service.user.passport.UserLoginService;
import com.itellyou.service.user.UserSingleService;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import com.itellyou.util.validation.Mobile;
import com.itellyou.util.validation.MobileValidator;
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

    private final UserSingleService userSearchService;
    private final GeetestService geetestService;
    private final UserLoginService loginService;
    private final SmsLogService smsLogService;
    private final SysPermissionService permissionService;

    @Autowired
    public LoginController(UserSingleService userSearchService, GeetestService geetestService, UserLoginService loginService, SmsLogService smsLogService, SysPermissionService permissionService){
        this.userSearchService = userSearchService;
        this.geetestService = geetestService;
        this.loginService = loginService;
        this.smsLogService = smsLogService;
        this.permissionService = permissionService;
    }

    @PostMapping("/account")
    public ResultModel account(HttpServletRequest request, HttpServletResponse response, @MultiRequestBody @NotBlank String username, @MultiRequestBody @NotBlank String password, @MultiRequestBody("geetest") GeetestResultModel geetestResultModel,@MultiRequestBody(required = false) String p){
        boolean result = geetestService.verify(geetestResultModel);
        if(!result){
            return new ResultModel(1001,"Geetest 验证失败");
        }
        UserInfoModel userInfoModel = null;
        if(new MobileValidator().isValid(username,null)){
            userInfoModel = userSearchService.findByMobile(username);
            if(userInfoModel == null){
                return new ResultModel(1002,"未注册的手机号");
            }else if(userInfoModel.isMobileStatus() == false){
                return new ResultModel(10021,"未激活的手机号");
            }

        }else if(new EmailValidator().isValid(username,null)){
            userInfoModel = userSearchService.findByEmail(username);
            if(userInfoModel == null){
                return new ResultModel(1003,"未注册的邮箱");
            }else if(userInfoModel.isMobileStatus() == false){
                return new ResultModel(10031,"未激活的邮箱");
            }
        }else{
            userInfoModel = userSearchService.findByLoginName(username);
            if(userInfoModel == null){
                return new ResultModel(1004,"用户名不存在");
            }
        }
        if(userInfoModel.getLoginPassword().equals(StringUtils.encoderPassword(password))){
            if(userInfoModel.isDisabled()){
                return new ResultModel(1006,"账户已锁定");
            }
            String clientIp = IPUtils.getClientIp(request);
            String token = loginService.createToken(userInfoModel.getId(),IPUtils.toLong(clientIp));
            if(!StringUtils.isNotEmpty(token)){
                return new ResultModel(1007,"登录出错啦");
            }
            loginService.sendToken(response,token);
            SysPermissionPlatform platform = SysPermissionPlatform.WEB;
            try{
                if(StringUtils.isNotEmpty(p)){
                    platform = SysPermissionPlatform.valueOf(p.toUpperCase());
                }
            }catch (Exception e){}
            List<SysPermissionModel> permissionModels = permissionService.search(userInfoModel.getId(), platform,null,null,null,null,null,null);
            return new ResultModel(userInfoModel,"base").
                    extend("access",permissionModels);
        }
        return new ResultModel(1005,"密码错误");
    }

    @PostMapping("/mobile")
    public ResultModel mobile(HttpServletRequest request, HttpServletResponse response, @MultiRequestBody @NotBlank @Mobile String mobile, @MultiRequestBody @NotBlank String code,@MultiRequestBody(required = false) String p){

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
            return new ResultModel(1001,"验证码错误或已过期");
        }
        UserInfoModel userInfoModel = userSearchService.findByMobile(mobile);
        if(userInfoModel == null){
            return new ResultModel(1002,"未注册的手机号");
        }
        if(userInfoModel.isDisabled()){
            return new ResultModel(1003,"账户已锁定");
        }
        String clientIp = IPUtils.getClientIp(request);
        String token = loginService.createToken(userInfoModel.getId(),IPUtils.toLong(clientIp));
        if(!StringUtils.isNotEmpty(token)){
            return new ResultModel(1004,"登录出错啦");
        }
        loginService.sendToken(response,token);
        SysPermissionPlatform platform = SysPermissionPlatform.WEB;
        try{
            if(StringUtils.isNotEmpty(p)){
                platform = SysPermissionPlatform.valueOf(p.toUpperCase());
            }
        }catch (Exception e){}
        List<SysPermissionModel> permissionModels = permissionService.search(userInfoModel.getId(), platform,null,null,null,null,null,null);
        return new ResultModel(userInfoModel,"base").
                extend("access",permissionModels);
    }
}
