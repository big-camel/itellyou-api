package com.itellyou.api.controller.oauth;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.thirdparty.*;
import com.itellyou.model.user.*;
import com.itellyou.service.thirdparty.*;
import com.itellyou.service.user.*;
import com.itellyou.service.user.passport.UserLoginService;
import com.itellyou.service.user.passport.UserRegisterService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import com.itellyou.util.validation.Mobile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/oauth/github")
public class GithubOAuthController {

    private final ThirdAccountService accountService;
    private final ThirdLogService logService;
    private final UserLoginService loginService;
    private final GithubService githubService;
    private final SmsLogService smsLogService;
    private final UserSingleService userSearchService;
    private final UserRegisterService userRegisterService;
    private final static String GITHUB_USER_SESSION_KEY = "github_user";

    public GithubOAuthController(ThirdAccountService accountService, ThirdLogService logService, UserLoginService loginService, GithubService githubService, SmsLogService smsLogService, UserSingleService userSearchService, UserRegisterService userRegisterService) {
        this.accountService = accountService;
        this.logService = logService;
        this.loginService = loginService;
        this.githubService = githubService;
        this.smsLogService = smsLogService;
        this.userSearchService = userSearchService;
        this.userRegisterService = userRegisterService;
    }

    @GetMapping("")
    public ResultModel github(HttpServletRequest request, HttpServletResponse response, UserInfoModel userModel, @RequestParam String action){
        try {
            String referer = request.getHeader("Referer");
            ThirdAccountAction accountAction = ThirdAccountAction.valueOf(action.toUpperCase());
            if(userModel == null && accountAction.equals(ThirdAccountAction.BIND)){
                return new ResultModel(401,"未登录");
            }
            Long userId = userModel == null ? 0l : userModel.getId();
            String url = accountService.oauthGithubURL(userId,accountAction,referer,IPUtils.toLong(IPUtils.getClientIp(request)));
            if(accountAction.equals(ThirdAccountAction.LOGIN)){
                response.sendRedirect(url);
            }
            return new ResultModel(url);
        }catch (Exception e){
            return new ResultModel(0,e.getLocalizedMessage());
        }
    }

    @GetMapping("/callback")
    public void callback(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestParam(name = "code") String authCode, @RequestParam String state) throws IOException {
        try {
            ThirdLogModel logModel = logService.find(state);
            if(logModel == null || logModel.isVerify()) {
                throw new Exception("错误的 state");
            }
            int result = logService.updateVerify(true,logModel.getId());
            if(result != 1) throw new Exception("验证异常");
            Long ip = IPUtils.toLong(IPUtils.getClientIp(request));
            if(logModel.getAction().equals(ThirdAccountAction.BIND)){
                result = accountService.bindGithub(logModel.getCreatedUserId(),authCode,ip);
                if(result != 1) throw new Exception("绑定失败");
            }else if (logModel.getAction().equals(ThirdAccountAction.LOGIN)){
                String accessToken = githubService.getOAuthToken(authCode);
                if(StringUtils.isEmpty(accessToken)){
                    throw new Exception("验证失败");
                }
                JSONObject jsonObject = githubService.getUserInfo(accessToken);
                if(jsonObject == null || StringUtils.isEmpty(jsonObject.getString("id"))) throw new Exception("验证失败");
                String githubUserId = jsonObject.getString("id");
                URL redirectUrl = new URL(logModel.getRedirectUri());
                ThirdAccountModel accountModel = accountService.searchByTypeAndKey(ThirdAccountType.GITHUB,githubUserId);
                if(accountModel == null){
                    // 注册
                    Map<String , String > userMap = new HashMap<>();
                    userMap.put("key",githubUserId);
                    userMap.put("name",jsonObject.getString("login"));
                    userMap.put("avatar",jsonObject.getString("avatar_url"));
                    userMap.put("home",jsonObject.getString("html_url"));
                    userMap.put("star",jsonObject.getString("followers"));
                    session.setAttribute(GITHUB_USER_SESSION_KEY,userMap);
                    StringBuilder urlBuilder = new StringBuilder(redirectUrl.getProtocol()).append("://").append(redirectUrl.getAuthority()).append("/login/oauth?type=github");
                    logModel.setRedirectUri(urlBuilder.toString());
                }else {
                    String token = loginService.createToken(accountModel.getUserId(),ip);
                    loginService.sendToken(response,token);

                    StringBuilder urlBuilder = new StringBuilder(redirectUrl.getProtocol()).append("://").append(redirectUrl.getAuthority()).append("/dashboard");
                    logModel.setRedirectUri(urlBuilder.toString());
                }
            }
            String uri = logModel.getRedirectUri();
            URL url = new URL(uri);

            if(!new HashSet<String>(){{add("localhost");add("www.yanmao.cc");add("www.aomao.com");}}.contains(url.getHost()))
                uri = "https://www.aomao.com";
            response.sendRedirect(uri);
        }catch (Exception e){
            e.printStackTrace();
            response.sendRedirect("https://www.aomao.com/500");
        }
    }

    @PostMapping("/login")
    public ResultModel login(HttpServletRequest request, HttpServletResponse response, HttpSession session, @MultiRequestBody @NotBlank @Mobile String mobile, @MultiRequestBody @NotBlank String code){

        List<SmsLogModel> listLog = smsLogService.searchByTemplateAndMobile("verify",mobile);
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
        Object sessionData = session.getAttribute(GITHUB_USER_SESSION_KEY);
        if(sessionData == null) return new ResultModel(500,"认证错误，请返回重试");
        Map<String , String> userMap = (Map<String , String>)sessionData;

        String clientIp = IPUtils.getClientIp(request);
        UserInfoModel userInfoModel = userSearchService.findByMobile(mobile);
        Long userId = userInfoModel == null ? null : userInfoModel.getId();
        String nickName = userInfoModel == null ? null : userInfoModel.getName();
        String name = userMap.get("name").trim();
        if(userId == null){
            nickName = name;
            if(StringUtils.isEmpty(nickName)){
                nickName = "u_" + StringUtils.randomString(10);
            }
            UserInfoModel nameModel = userSearchService.findByName(nickName);
            if(nameModel != null){
                nickName = name + "_" + StringUtils.randomString(10);
            }
            userId = userRegisterService.mobile(nickName,null,mobile,clientIp);
        }
        ThirdAccountModel accountModel = new ThirdAccountModel();
        accountModel.setUserId(userId);
        accountModel.setCreatedTime(DateUtils.toLocalDateTime());
        accountModel.setCreatedIp(IPUtils.toLong(clientIp));
        accountModel.setKey(userMap.get("key"));
        accountModel.setType(ThirdAccountType.GITHUB);
        accountModel.setName(StringUtils.isEmpty(name) ? nickName : name);
        accountModel.setAvatar(userMap.get("avatar"));
        accountModel.setHome(userMap.get("home"));
        accountModel.setStar(Long.valueOf(userMap.getOrDefault("star","0")));
        try {
            accountService.insert(accountModel);
        }catch (Exception e){
            return new ResultModel(500,"绑定失败，请重试");
        }
        if(userInfoModel != null && userInfoModel.isDisabled()){
            return new ResultModel(1003,"账户已锁定");
        }
        String token = loginService.createToken(userId,IPUtils.toLong(clientIp));
        if(StringUtils.isEmpty(token)){
            return new ResultModel(1004,"登录出错啦");
        }
        loginService.sendToken(response,token);
        session.removeAttribute(GITHUB_USER_SESSION_KEY);
        return new ResultModel(userInfoModel,"base");
    }
}
