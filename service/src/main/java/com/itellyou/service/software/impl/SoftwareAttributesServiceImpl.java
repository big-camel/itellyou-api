package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareAttributesDao;
import com.itellyou.model.software.SoftwareAttributesModel;
import com.itellyou.service.software.SoftwareAttributesService;
import com.itellyou.util.RedisUtils;
import com.itellyou.util.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = "software_attributes")
@Service
public class SoftwareAttributesServiceImpl implements SoftwareAttributesService {

    private final SoftwareAttributesDao attributesDao;

    public SoftwareAttributesServiceImpl(SoftwareAttributesDao attributesDao) {
        this.attributesDao = attributesDao;
    }

    @Override
    @CacheEvict(value = "software_attributes" , allEntries = true)
    public int add(SoftwareAttributesModel model) {
        return attributesDao.add(model);
    }

    @Override
    @CacheEvict(value = "software_attributes" , allEntries = true)
    public int addAll(HashSet<SoftwareAttributesModel> attributesValues) {
        return attributesDao.addAll(attributesValues);
    }

    @Override
    @CacheEvict(value = "software_attributes" , allEntries = true)
    public int clear(Long softwareId) {
        return attributesDao.clear(softwareId);
    }

    @Override
    @CacheEvict(value = "software_attributes" , allEntries = true)
    public int remove(Long id) {
        return attributesDao.remove(id);
    }

    @Override
    public List<SoftwareAttributesModel> search(HashSet<Long> softwareIds) {
        StringBuilder keySb = new StringBuilder();
        for (Long id : softwareIds){
            keySb.append(id);
        }
        String key = StringUtils.md5(keySb.toString());
        List<SoftwareAttributesModel> cacheData = RedisUtils.getCache("software_attributes",key,List.class);
        if(cacheData == null || cacheData.size() == 0)
        {
            cacheData = attributesDao.search(softwareIds);
            RedisUtils.setCache("software_attributes",key,cacheData);
        }
        return cacheData;
    }
}
