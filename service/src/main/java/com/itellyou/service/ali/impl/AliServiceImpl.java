package com.itellyou.service.ali.impl;

import com.aliyuncs.*;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.itellyou.model.ali.AliConfigModel;
import com.itellyou.service.ali.AliConfigService;
import com.itellyou.service.ali.AliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AliServiceImpl implements AliService {

    private final AliConfigService aliConfigService;

    @Autowired
    public AliServiceImpl(AliConfigService aliConfigService) {
        this.aliConfigService = aliConfigService;
    }

    @Override
    public DefaultAcsClient getClient() {
        return getClient("cn-hangzhou");
    }

    @Override
    public DefaultAcsClient getClient(String regionId) {
        AliConfigModel aliConfigModel = this.aliConfigService.get();

        DefaultProfile profile = DefaultProfile.getProfile(regionId, aliConfigModel.getId(), aliConfigModel.getSecret());
        return new DefaultAcsClient(profile);
    }
}
