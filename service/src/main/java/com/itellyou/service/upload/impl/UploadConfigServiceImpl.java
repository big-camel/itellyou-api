package com.itellyou.service.upload.impl;

import com.itellyou.dao.upload.UploadConfigDao;
import com.itellyou.model.upload.UploadConfigModel;
import com.itellyou.model.upload.UploadType;
import com.itellyou.service.upload.UploadConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UploadConfigServiceImpl implements UploadConfigService {

    private final UploadConfigDao uploadConfigDao;

    @Autowired
    public UploadConfigServiceImpl(UploadConfigDao uploadConfigDao){
        this.uploadConfigDao = uploadConfigDao;
    }

    @Override
    public UploadConfigModel get(UploadType type) {
        return uploadConfigDao.get(type);
    }

    @Override
    public Map<String, UploadConfigModel> getAll() {
        return uploadConfigDao.getAll();
    }
}
