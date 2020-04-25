package com.itellyou.service.ali.impl;

import com.itellyou.dao.ali.DmConfigDao;
import com.itellyou.model.ali.DmConfigModel;
import com.itellyou.service.ali.DmConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DmConfigServiceImpl implements DmConfigService {

    @Autowired
    private DmConfigDao configDao;

    @Override
    public DmConfigModel get(String type) {
        return configDao.get(type);
    }

    @Override
    public Map<String, DmConfigModel> getAll() {
        return configDao.getAll();
    }
}
