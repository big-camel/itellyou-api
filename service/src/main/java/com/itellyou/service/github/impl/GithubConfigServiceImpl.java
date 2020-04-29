package com.itellyou.service.github.impl;

import com.itellyou.dao.github.GithubConfigDao;
import com.itellyou.model.ali.AliConfigModel;
import com.itellyou.model.github.GithubConfigModel;
import com.itellyou.service.ali.AliConfigService;
import com.itellyou.service.github.GithubConfigService;
import org.springframework.stereotype.Service;

@Service
public class GithubConfigServiceImpl implements GithubConfigService {

    private final GithubConfigDao configDao;

    public GithubConfigServiceImpl(GithubConfigDao configDao) {
        this.configDao = configDao;
    }

    public GithubConfigModel get(){
        return configDao.get();
    }
}
