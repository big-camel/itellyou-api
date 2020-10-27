package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareAttributesDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.software.SoftwareAttributesModel;
import com.itellyou.service.software.SoftwareAttributesService;
import com.itellyou.util.RedisUtils;
import com.itellyou.util.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@CacheConfig(cacheNames = CacheKeys.SOFTWARE_ATTRIBUTES_KEY)
@Service
public class SoftwareAttributesServiceImpl implements SoftwareAttributesService {

    private final SoftwareAttributesDao attributesDao;

    public SoftwareAttributesServiceImpl(SoftwareAttributesDao attributesDao) {
        this.attributesDao = attributesDao;
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_ATTRIBUTES_KEY , allEntries = true)
    public int add(SoftwareAttributesModel model) {
        return attributesDao.add(model);
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_ATTRIBUTES_KEY , allEntries = true)
    public int addAll(Collection<SoftwareAttributesModel> attributesValues) {
        return attributesDao.addAll(attributesValues);
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_ATTRIBUTES_KEY , allEntries = true)
    public int clear(Long softwareId) {
        return attributesDao.clear(softwareId);
    }

    @Override
    @CacheEvict(value = CacheKeys.SOFTWARE_ATTRIBUTES_KEY , allEntries = true)
    public int remove(Long id) {
        return attributesDao.remove(id);
    }

    @Override
    public List<SoftwareAttributesModel> search(Collection<Long> softwareIds) {
        StringBuilder keySb = new StringBuilder();
        for (Long id : softwareIds){
            keySb.append(id);
        }
        String key = StringUtils.md5(keySb.toString());
        List<SoftwareAttributesModel> cacheData = RedisUtils.get(CacheKeys.SOFTWARE_ATTRIBUTES_KEY,key,List.class);
        if(cacheData == null || cacheData.size() == 0)
        {
            cacheData = attributesDao.search(softwareIds);
            RedisUtils.set(CacheKeys.SOFTWARE_ATTRIBUTES_KEY,key,cacheData);
        }
        return cacheData;
    }
}
