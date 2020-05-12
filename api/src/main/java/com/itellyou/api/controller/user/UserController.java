package com.itellyou.api.controller.user;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.SysPath;
import com.itellyou.model.sys.SysPathModel;
import com.itellyou.model.sys.SysPermissionModel;
import com.itellyou.model.sys.SysPermissionPlatform;
import com.itellyou.model.user.UserBankModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserRankModel;
import com.itellyou.service.sys.SysPathService;
import com.itellyou.service.sys.SysPermissionService;
import com.itellyou.service.user.*;
import com.itellyou.util.CookieUtils;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import com.itellyou.util.serialize.filter.Labels;
import com.itellyou.util.serialize.filter.PrivacyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.List;

@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserInfoService userInfoService;
    private final UserSearchService userSearchService;
    private final UserLoginLogService userLoginLogService;
    private final SysPathService pathService;
    private final SysPermissionService permissionService;
    private final UserRankService userRankService;
    private final UserBankService bankService;

    @Autowired
    public UserController(UserInfoService userInfoService, UserSearchService userSearchService, UserLoginLogService userLoginLogService, SysPathService pathService, SysPermissionService permissionService, UserRankService userRankService, UserBankService bankService){
        this.userInfoService = userInfoService;
        this.userSearchService = userSearchService;
        this.userLoginLogService = userLoginLogService;
        this.pathService = pathService;
        this.permissionService = permissionService;
        this.userRankService = userRankService;
        this.bankService = bankService;
    }

    @GetMapping("/me")
    public ResultModel me(UserInfoModel userModel,@RequestParam(required = false) String p){
        if(userModel == null) return new ResultModel(401,"No Access");
        SysPathModel pathModel = pathService.findByTypeAndId(SysPath.USER,userModel.getId());
        SysPermissionPlatform platform = SysPermissionPlatform.WEB;
        try{
            if(StringUtils.isNotEmpty(p)){
                platform = SysPermissionPlatform.valueOf(p.toUpperCase());
            }
        }catch (Exception e){}
        UserRankModel rankModel = userRankService.find(userModel.getId());
        UserBankModel bankModel = bankService.findByUserId(userModel.getId());
        List<SysPermissionModel> permissionModels = permissionService.search(userModel.getId(), platform,null,null,null,null,null,null);
        return new ResultModel(userModel).
                extend("path",pathModel == null ? null : pathModel.getPath()).
                extend("access",permissionModels).
                extend("rank",rankModel).
                extend("bank",bankModel);
    }

    @GetMapping("/account")
    public ResultModel account(UserInfoModel userModel){
        ResultModel resultModel = new ResultModel(userModel,new Labels.LabelModel(UserInfoModel.class,"base","account"));
        resultModel.extend("is_set_pwd", StringUtils.isNotEmpty(userModel.getLoginPassword()));
        SysPathModel pathModel = pathService.findByTypeAndId(SysPath.USER,userModel.getId());
        resultModel.extend("path",pathModel == null ? null : pathModel.getPath())
        .addFilters(new PrivacyFilter());
        return resultModel;
    }

    @GetMapping("/{id:\\d+}")
    public ResultModel find(UserInfoModel currentModel, @PathVariable Long id){
        Long searchUserId = currentModel == null ? null : currentModel.getId();
        List<UserDetailModel> models = userSearchService.search(new HashSet<Long>(){{add(id);}},searchUserId,null,null,null,null,null,null,null,null,null,null);
        UserDetailModel detailModel = models == null || models.size() == 0 ? null : models.get(0);
        if(detailModel == null){
            return new ResultModel(404,"错误的用户ID");
        }

        ResultModel resultModel = new ResultModel(detailModel,new Labels.LabelModel(UserDetailModel.class,"base","info"));
        return resultModel;
    }

    @PostMapping("/query/name")
    public ResultModel queryName(@MultiRequestBody @NotBlank String name){
        UserInfoModel userModel = userSearchService.findByName(name);
        if(userModel != null){
            return new ResultModel(500,"昵称不可用",name);
        }
        return new ResultModel(name);
    }

    @PutMapping("/profile")
    public ResultModel profile(HttpServletRequest request, UserInfoModel userModel, @MultiRequestBody(required = false) String avatar, @MultiRequestBody(required = false) Integer gender,
                               @MultiRequestBody(required = false) String name, @MultiRequestBody(required = false) String description,
                               @MultiRequestBody(required = false) String introduction, @MultiRequestBody(required = false) String profession,
                               @MultiRequestBody(required = false) String address){
        if(userModel == null){
            return new ResultModel(401,"未登录");
        }
        if(name != null){
            if(!StringUtils.isNotEmpty(name)) return new ResultModel(0,"昵称格式不正确",name);
            UserInfoModel infoModel = userSearchService.findByName(name);
            if(infoModel != null && !infoModel.getId().equals(userModel.getId())){
                return new ResultModel(0,"昵称不可用",name);
            }
            name = name.trim();
        }

        String ip = IPUtils.getClientIp(request);
        UserInfoModel updateModel = new UserInfoModel(
                userModel.getId(),null,
                name,null,null,gender,null,null,userModel.isMobileStatus(),null,userModel.isEmailStatus(),
                description,introduction,profession,address,avatar,userModel.isDisabled(),userModel.getId(),
                DateUtils.getTimestamp(),IPUtils.toLong(ip)
                );

        int result = userInfoService.updateByUserId(updateModel);
        if(result == 1){
            return new ResultModel(userSearchService.findById(userModel.getId()));
        }
        return new ResultModel(0,"更新失败");
    }

    @GetMapping("/logout")
    public ResultModel logout(HttpServletRequest request, HttpServletResponse response, UserInfoModel userModel){
        if(userModel != null){
            Object attributeValue = request.getAttribute("token");
            String token = attributeValue == null ? null : attributeValue.toString();
            if(!StringUtils.isNotEmpty(token)){
                Cookie cookie = CookieUtils.getCookie(request,"token");
                if(cookie != null){
                    token = cookie.getValue();
                }
            }
            if(StringUtils.isNotEmpty(token)){
                userLoginLogService.setDisabled(true,token);
            }
        }
        CookieUtils.removeCookie(response,"token");
        return new ResultModel();
    }
}
