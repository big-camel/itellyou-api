package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareVersionDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.software.SoftwareVersionModel;
import com.itellyou.service.software.SoftwareVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = CacheKeys.SOFTWARE_VERSION_KEY)
@Service
public class SoftwareVersionServiceImpl implements SoftwareVersionService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SoftwareVersionDao versionDao;

    @Autowired
    public SoftwareVersionServiceImpl(SoftwareVersionDao softwareVersionDao){
        this.versionDao = softwareVersionDao;
    }

    @Override
    public int insert(SoftwareVersionModel softwareVersionModel) {
        try{
            int id = versionDao.insert(softwareVersionModel);
            if(id < 1){
                throw new Exception("写入版本失败");
            }
            Integer version = versionDao.findVersionById(softwareVersionModel.getId());
            softwareVersionModel.setVersion(version);
            return id;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            return 0;
        }
    }
}
