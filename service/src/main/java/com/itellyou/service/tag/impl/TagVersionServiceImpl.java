package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagVersionDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.tag.TagVersionModel;
import com.itellyou.service.tag.TagVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = CacheKeys.TAG_VERSION_KEY)
@Service
public class TagVersionServiceImpl implements TagVersionService {

    private final TagVersionDao versionDao;

    @Autowired
    public TagVersionServiceImpl(TagVersionDao versionDao){
        this.versionDao = versionDao;
    }

    @Override
    public int insert(TagVersionModel versionModel) {
        try{
            int rows = versionDao.insert(versionModel);
            if(rows != 1){
                return rows;
            }
            Integer version = versionDao.findVersionById(versionModel.getId());
            versionModel.setVersion(version);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}
