package com.itellyou.service.thirdparty.impl;

import com.aliyuncs.*;
import com.aliyuncs.profile.DefaultProfile;
import com.itellyou.model.thirdparty.AliConfigModel;
import com.itellyou.service.thirdparty.AliService;
import com.itellyou.service.common.ConfigDefaultService;
import org.springframework.stereotype.Service;

@Service
public class AliServiceImpl implements AliService {

    private final ConfigDefaultService<AliConfigModel> configDefaultService;

    public AliServiceImpl(AliConfigDefaultServiceImpl configServer) {
        this.configDefaultService = configServer;
    }

    @Override
    public DefaultAcsClient getClient() {
        return getClient("cn-hangzhou");
    }

    @Override
    public DefaultAcsClient getClient(String regionId) {
        AliConfigModel aliConfigModel = configDefaultService.getDefault();

        DefaultProfile profile = DefaultProfile.getProfile(regionId, aliConfigModel.getId(), aliConfigModel.getSecret());
        return new DefaultAcsClient(profile);
    }
}
