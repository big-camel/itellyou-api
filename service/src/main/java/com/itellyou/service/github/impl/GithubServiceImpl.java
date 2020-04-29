package com.itellyou.service.github.impl;

import com.alibaba.fastjson.JSONObject;
import com.itellyou.model.github.GithubConfigModel;
import com.itellyou.service.github.GithubConfigService;
import com.itellyou.service.github.GithubService;
import com.itellyou.util.HttpClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

@Service
public class GithubServiceImpl implements GithubService {

    private final GithubConfigService configService;

    public GithubServiceImpl(GithubConfigService configService) {
        this.configService = configService;
    }

    @Override
    public String getOAuthToken(String code) {
        GithubConfigModel configModel = configService.get();
        if(configModel == null) return null;
        MultiValueMap<String,String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("client_id",configModel.getId());
        paramMap.add("client_secret",configModel.getSecret());
        paramMap.add("code",code);
        HttpHeaders httpHeaders = new HttpHeaders();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(mediaTypes);
        String body = HttpClient.post("https://github.com/login/oauth/access_token",paramMap,httpHeaders);
        try{
            JSONObject jsonObject = JSONObject.parseObject(body);
            return jsonObject.getString("access_token");
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public JSONObject getUserInfo(String accessToken) {
        MultiValueMap<String,String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("access_token",accessToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(mediaTypes);
        String body = HttpClient.get("https://api.github.com/user",paramMap,httpHeaders);
        try{
            return JSONObject.parseObject(body);
        }catch (Exception e){
            return null;
        }
    }
}
