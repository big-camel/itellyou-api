package com.itellyou.service.ali.impl;

import com.itellyou.dao.ali.AliConfigDao;
import com.itellyou.model.ali.AliConfigModel;
import com.itellyou.service.ali.AliConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AliCofigServiceImpl implements AliConfigService {

    @Autowired
    private AliConfigDao aliConfigDao;

    public AliConfigModel get(){
        return aliConfigDao.get();
    }
}
