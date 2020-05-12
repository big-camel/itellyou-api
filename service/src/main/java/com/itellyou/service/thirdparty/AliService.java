package com.itellyou.service.thirdparty;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;

public interface AliService {
    DefaultAcsClient getClient();

    DefaultAcsClient getClient(String regionId);

}
