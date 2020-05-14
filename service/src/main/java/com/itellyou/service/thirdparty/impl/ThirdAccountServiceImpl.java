package com.itellyou.service.thirdparty.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.itellyou.dao.thirdparty.ThirdAccountDao;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.event.OperationalEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.thirdparty.*;
import com.itellyou.service.common.ConfigDefaultService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.thirdparty.AlipayService;
import com.itellyou.service.thirdparty.GithubService;
import com.itellyou.service.thirdparty.ThirdAccountService;
import com.itellyou.service.thirdparty.ThirdLogService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "user_third_account")
@Service
public class ThirdAccountServiceImpl implements ThirdAccountService {

    private final ThirdAccountDao accountDao;
    private final ThirdLogService logService;
    private final AlipayService alipayService;
    private final GithubService githubService;
    private final ConfigDefaultService<AliPayConfigModel> alipayConfigService;
    private final ConfigDefaultService<GithubConfigModel> githubConfigService;
    private final OperationalPublisher operationalPublisher;

    public ThirdAccountServiceImpl(ThirdAccountDao accountDao, ThirdLogService logService, AlipayService alipayService, GithubService githubService, AlipayConfigDefaultServiceImpl alipayConfigService, GithubConfigServiceImpl githubConfigService, OperationalPublisher operationalPublisher) {
        this.accountDao = accountDao;
        this.logService = logService;
        this.alipayService = alipayService;
        this.githubService = githubService;
        this.alipayConfigService = alipayConfigService;
        this.githubConfigService = githubConfigService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    public int insert(ThirdAccountModel model) throws Exception {
        return accountDao.insert(model);
    }

    @Override
    @CacheEvict(key = "#userId")
    public int deleteByUserIdAndType(Long userId, ThirdAccountType type,Long ip) {
        int result = accountDao.deleteByUserIdAndType(userId,type);
        if(result == 1){
            EntityType entityType;
            if(type.equals(ThirdAccountType.GITHUB))
                entityType = EntityType.GITHUB;
            else if(type.equals(ThirdAccountType.ALIPAY))
                entityType = EntityType.ALIPAY;
            else return result;

            OperationalModel operationalModel = new OperationalModel(EntityAction.UNBIND, entityType,userId,userId,userId,DateUtils.getTimestamp(), ip);
            operationalPublisher.publish(new OperationalEvent(this,operationalModel));
        }
        return result;
    }

    @Override
    @Cacheable
    public Map<String, ThirdAccountModel> searchByUserId(Long userId) {
        Map<ThirdAccountType, ThirdAccountModel> thirdAccountModelMap = accountDao.searchByUserId(userId);
        Map<String, ThirdAccountModel> stringMap = new HashMap<>();
        for (Map.Entry<ThirdAccountType,ThirdAccountModel> entry : thirdAccountModelMap.entrySet()){
            stringMap.put(entry.getKey().getName(),entry.getValue());
        }
        return stringMap;
    }

    @Override
    public ThirdAccountModel searchByTypeAndKey(ThirdAccountType type, String key) {
        return accountDao.searchByTypeAndKey(type,key);
    }

    @Override
    public List<ThirdAccountModel> search(Long userId, ThirdAccountType type, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return accountDao.search(userId,type,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(Long userId, ThirdAccountType type, Long beginTime, Long endTime, Long ip) {
        return accountDao.count(userId,type,beginTime,endTime,ip);
    }

    @Override
    public PageModel<ThirdAccountModel> page(Long userId, ThirdAccountType type, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<ThirdAccountModel> data = search(userId,type,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(userId,type,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public String oauthAlipayURL(Long userId, ThirdAccountAction action, String redirectUri, Long ip) throws Exception {
        AliPayConfigModel configModel = alipayConfigService.getDefault();
        if(configModel == null) throw new Exception("未启用支付宝接口配置");
        StringBuilder url = new StringBuilder("https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?");
        url.append("app_id=").append(configModel.getAppId()).append("&");
        url.append("scope=").append("auth_user").append("&");
        url.append("redirect_uri=").append(URLEncoder.encode(configModel.getRedirectUri(),"UTF-8"));
        ThirdLogModel logModel = new ThirdLogModel();
        logModel.setAction(action);
        logModel.setType(ThirdAccountType.ALIPAY);
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
    public String oauthGithubURL(Long userId, ThirdAccountAction action, String redirectUri, Long ip) throws Exception {
        GithubConfigModel configModel = githubConfigService.getDefault();
        if(configModel == null) throw new Exception("未启用Github接口配置");
        StringBuilder url = new StringBuilder(configModel.getGateway());
        url.append("?").append("client_id=").append(configModel.getId()).append("&");
        url.append("redirect_uri=").append(URLEncoder.encode(configModel.getRedirectUri(),"UTF-8"));
        ThirdLogModel logModel = new ThirdLogModel();
        logModel.setAction(action);
        logModel.setType(ThirdAccountType.GITHUB);
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
        ThirdAccountModel accountModel = new ThirdAccountModel();
        String name = shareResponse.getNickName();
        if(StringUtils.isEmpty(name))
            name = StringUtils.randomString(10).toUpperCase();
        accountModel.setUserId(userId);
        accountModel.setType(ThirdAccountType.ALIPAY);
        accountModel.setName(name);
        accountModel.setKey(shareResponse.getUserId());
        accountModel.setAvatar(shareResponse.getAvatar());
        accountModel.setCreatedIp(ip);
        accountModel.setCreatedTime(DateUtils.getTimestamp());
        int result = this.insert(accountModel);
        if(result == 1){
            OperationalModel operationalModel = new OperationalModel(EntityAction.BIND, EntityType.ALIPAY,userId,userId,userId,DateUtils.getTimestamp(), ip);
            operationalPublisher.publish(new OperationalEvent(this,operationalModel));
        }
        return result;
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
        ThirdAccountModel accountModel = new ThirdAccountModel();
        String name = jsonObject.getString("login");
        if(StringUtils.isEmpty(name))
            name = StringUtils.randomString(10).toUpperCase();
        accountModel.setUserId(userId);
        accountModel.setType(ThirdAccountType.GITHUB);
        accountModel.setName(name);
        accountModel.setKey(jsonObject.getString("id"));
        accountModel.setAvatar(jsonObject.getString("avatar_url"));
        accountModel.setHome(jsonObject.getString("html_url"));
        accountModel.setStar(jsonObject.getLong("followers"));
        accountModel.setCreatedIp(ip);
        accountModel.setCreatedTime(DateUtils.getTimestamp());
        int result = this.insert(accountModel);
        if(result == 1){
            OperationalModel operationalModel = new OperationalModel(EntityAction.BIND, EntityType.GITHUB,userId,userId,userId,DateUtils.getTimestamp(), ip);
            operationalPublisher.publish(new OperationalEvent(this,operationalModel));
        }
        return result;
    }
}
