package com.itellyou.api.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.thirdparty.SmsLogModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.thirdparty.SmsLogService;
import com.itellyou.service.user.passport.UserRegisterService;
import com.itellyou.service.user.UserSingleService;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Validated
@RestController
public class RegisterController {

    private final UserSingleService userSearchService;
    private final UserRegisterService userRegisterService;
    private final SmsLogService smsLogService;

    @Autowired
    public RegisterController(UserSingleService userSearchService,UserRegisterService userRegisterService,SmsLogService smsLogService){
        this.userSearchService = userSearchService;
        this.userRegisterService = userRegisterService;
        this.smsLogService = smsLogService;
    }

    @PostMapping("/user/register")
    public ResultModel register(HttpServletRequest request, @Validated(UserInfoModel.RegisterAction.class) @RequestBody UserInfoModel userInfoModel, @MultiRequestBody @NotBlank String code){
        String mobile = userInfoModel.getMobile();
        List<SmsLogModel> listLog = smsLogService.searchByTemplateAndMobile("register",mobile);
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

        UserInfoModel user = userSearchService.findByMobile(mobile);
        if(user != null){
            return new ResultModel(1002,"手机号已被注册",mobile);
        }
        String name = userInfoModel.getName().trim();
        if(!StringUtils.isNotEmpty(name)) return new ResultModel(1003,"昵称格式不正确",name);
        user = userSearchService.findByName(name);
        if(user != null){
            return new ResultModel(1003,"昵称不可用",name);
        }

        String clientIp = IPUtils.getClientIp(request);
        Long userId = userRegisterService.mobile(name,userInfoModel.getLoginPassword(),mobile,clientIp);
        if(userId != null && userId > 0){
            return new ResultModel(userId);
        }
        return new ResultModel(1004,"注册用户失败，请重试");
    }
}
