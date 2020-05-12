package com.itellyou.service.thirdparty;

import com.alibaba.fastjson.JSONObject;

public interface GithubService {

    String getOAuthToken(String code);

    JSONObject getUserInfo(String accessToken);
}
