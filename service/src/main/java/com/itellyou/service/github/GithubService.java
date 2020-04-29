package com.itellyou.service.github;

import com.alibaba.fastjson.JSONObject;

public interface GithubService {

    String getOAuthToken(String code);

    JSONObject getUserInfo(String accessToken);
}
