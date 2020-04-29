package com.itellyou.service.user.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.itellyou.dao.user.UserThirdAccountDao;
import com.itellyou.model.ali.AliPayConfigModel;
import com.itellyou.model.github.GithubConfigModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserThirdAccountAction;
import com.itellyou.model.user.UserThirdAccountModel;
import com.itellyou.model.user.UserThirdAccountType;
import com.itellyou.model.user.UserThirdLogModel;
import com.itellyou.service.ali.AlipayConfigService;
import com.itellyou.service.ali.AlipayService;
import com.itellyou.service.github.GithubConfigService;
import com.itellyou.service.github.GithubService;
import com.itellyou.service.user.UserThirdAccountService;
import com.itellyou.service.user.UserThirdLogService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Service
public class UserThirdAccountServiceImpl implements UserThirdAccountService {

    private final UserThirdAccountDao accountDao;
    private final UserThirdLogService logService;
    private final AlipayService alipayService;
    private final GithubService githubService;
    private final AlipayConfigService alipayConfigService;
    private final GithubConfigService githubConfigService;

    public UserThirdAccountServiceImpl(UserThirdAccountDao accountDao, UserThirdLogService logService, AlipayService alipayService, GithubService githubService, AlipayConfigService alipayConfigService, GithubConfigService githubConfigService) {
        this.accountDao = accountDao;
        this.logService = logService;
        this.alipayService = alipayService;
        this.githubService = githubService;
        this.alipayConfigService = alipayConfigService;
        this.githubConfigService = githubConfigService;
    }

    @Override
    public int insert(UserThirdAccountModel model) throws Exception {
        return accountDao.insert(model);
    }

    @Override
    public int deleteByUserIdAndType(Long userId, UserThirdAccountType type) {
        return accountDao.deleteByUserIdAndType(userId,type);
    }

    @Override
    public Map<UserThirdAccountType, UserThirdAccountModel> searchByUserId(Long userId) {
        return accountDao.searchByUserId(userId);
    }

    @Override
    public UserThirdAccountModel searchByTypeAndKey(UserThirdAccountType type, String key) {
        return accountDao.searchByTypeAndKey(type,key);
    }

    @Override
    public List<UserThirdAccountModel> search(Long userId, UserThirdAccountType type, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return accountDao.search(userId,type,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(Long userId, UserThirdAccountType type, Long beginTime, Long endTime, Long ip) {
        return accountDao.count(userId,type,beginTime,endTime,ip);
    }

    @Override
    public PageModel<UserThirdAccountModel> page(Long userId, UserThirdAccountType type, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<UserThirdAccountModel> data = search(userId,type,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(userId,type,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public String oauthAlipayURL(Long userId, UserThirdAccountAction action, String redirectUri, Long ip) throws Exception {
        AliPayConfigModel configModel = alipayConfigService.getDefault();
        if(configModel == null) throw new Exception("未启用支付宝接口配置");
        StringBuilder url = new StringBuilder("https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?");
        url.append("app_id=").append(configModel.getAppId()).append("&");
        url.append("scope=").append("auth_user").append("&");
        url.append("redirect_uri=").append(URLEncoder.encode(configModel.getRedirectUri(),"UTF-8"));
        UserThirdLogModel logModel = new UserThirdLogModel();
        logModel.setAction(action);
        logModel.setType(UserThirdAccountType.ALIPAY);
        logModel.setCreatedUserId(userId);
        logModel.setCreatedIp(ip);
        logModel.setCreatedTime(DateUtils.getTimestamp());
        logModel.setRedirectUri(redirectUri);
        int result = logService.insert(logModel);
        if(result != 1) throw new Exception("生成链接失败");
        url.append("&state=").append(logModel.getId());

        return url.toString();
    }

    @Override
    public String oauthGithubURL(Long userId, UserThirdAccountAction action, String redirectUri, Long ip) throws Exception {
        GithubConfigModel configModel = githubConfigService.get();
        if(configModel == null) throw new Exception("未启用Github接口配置");
        StringBuilder url = new StringBuilder(configModel.getGateway());
        url.append("?").append("client_id=").append(configModel.getId()).append("&");
        url.append("redirect_uri=").append(URLEncoder.encode(configModel.getRedirectUri(),"UTF-8"));
        UserThirdLogModel logModel = new UserThirdLogModel();
        logModel.setAction(action);
        logModel.setType(UserThirdAccountType.GITHUB);
        logModel.setCreatedUserId(userId);
        logModel.setCreatedIp(ip);
        logModel.setCreatedTime(DateUtils.getTimestamp());
        logModel.setRedirectUri(redirectUri);
        int result = logService.insert(logModel);
        if(result != 1) throw new Exception("生成链接失败");
        url.append("&state=").append(logModel.getId());

        return url.toString();
    }

    @Override
    public int bindAlipay(Long userId, String token, Long ip) throws Exception {
        AlipaySystemOauthTokenResponse tokenResponse = alipayService.getOAuthToken(token);
        if(tokenResponse == null || !tokenResponse.isSuccess()) {
            throw new Exception("错误的 token");
        }
        AlipayUserInfoShareResponse shareResponse = alipayService.getUserInfo(tokenResponse.getAccessToken());
        if(shareResponse == null || !shareResponse.isSuccess()){
            throw new Exception("错误的 access token");
        }
        UserThirdAccountModel accountModel = new UserThirdAccountModel();
        String name = shareResponse.getNickName();
        if(StringUtils.isEmpty(name))
            name = StringUtils.randomString(10).toUpperCase();
        accountModel.setUserId(userId);
        accountModel.setType(UserThirdAccountType.ALIPAY);
        accountModel.setName(name);
        accountModel.setKey(shareResponse.getUserId());
        accountModel.setAvatar(shareResponse.getAvatar());
        accountModel.setCreatedIp(ip);
        accountModel.setCreatedTime(DateUtils.getTimestamp());
        return this.insert(accountModel);
    }

    @Override
    public int bindGithub(Long userId, String token, Long ip) throws Exception {
        String accessToken = githubService.getOAuthToken(token);
        if(StringUtils.isEmpty(accessToken)) {
            throw new Exception("错误的 token");
        }
        JSONObject jsonObject = githubService.getUserInfo(accessToken);
        if(jsonObject == null || StringUtils.isEmpty(jsonObject.getString("id"))){
            throw new Exception("错误的 access token");
        }
        UserThirdAccountModel accountModel = new UserThirdAccountModel();
        String name = jsonObject.getString("login");
        if(StringUtils.isEmpty(name))
            name = StringUtils.randomString(10).toUpperCase();
        accountModel.setUserId(userId);
        accountModel.setType(UserThirdAccountType.GITHUB);
        accountModel.setName(name);
        accountModel.setKey(jsonObject.getString("id"));
        accountModel.setAvatar(jsonObject.getString("avatar_url"));
        accountModel.setHome(jsonObject.getString("html_url"));
        accountModel.setStar(jsonObject.getLong("followers"));
        accountModel.setCreatedIp(ip);
        accountModel.setCreatedTime(DateUtils.getTimestamp());
        return this.insert(accountModel);
    }
}
