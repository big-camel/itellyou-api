package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareTagDao;
import com.itellyou.model.software.SoftwareTagModel;
import com.itellyou.service.software.SoftwareTagService;
import com.itellyou.util.RedisUtils;
import com.itellyou.util.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@CacheConfig(cacheNames = "software_tag")
public class SoftwareTagServiceImpl implements SoftwareTagService {

    private final SoftwareTagDao softwareTagDao;

    public SoftwareTagServiceImpl(SoftwareTagDao softwareTagDao) {
        this.softwareTagDao = softwareTagDao;
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#model.softwareId") , @CacheEvict(value = "tag_software" , allEntries = true)})
    public int add(SoftwareTagModel model) {
        return softwareTagDao.add(model);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#softwareId") , @CacheEvict(value = "tag_software" , allEntries = true)})
    public int addAll(Long softwareId, HashSet<Long> tagIds) {
        return softwareTagDao.addAll(softwareId,tagIds);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#softwareId") , @CacheEvict(value = "tag_software" , allEntries = true)})
    public int clear(Long softwareId) {
        return softwareTagDao.clear(softwareId);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#softwareId") , @CacheEvict(value = "tag_software" , allEntries = true)})
    public int remove(Long softwareId, Long tagId) {
        return softwareTagDao.remove(softwareId,tagId);
    }

    @Override
    public Map<Long, List<SoftwareTagModel>> searchTags(HashSet<Long> softwareIds) {
        List<SoftwareTagModel> models = softwareTagDao.searchTags(softwareIds);
        Map<Long, List<SoftwareTagModel>> map = new LinkedHashMap<>();
        for (SoftwareTagModel model : models){
            if(!map.containsKey(model.getSoftwareId())){
                map.put(model.getSoftwareId(),new LinkedList<>());
            }
            map.get(model.getSoftwareId()).add(model);
        }
        return map;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public HashSet<Long> searchTagId(Long softwareId) {
        return softwareTagDao.searchTagId(softwareId);
    }

    @Override
    @Cacheable(value = "tag_software",key = "#tagId",unless = "#result == null")
    public HashSet<Long> searchSoftwareId(Long tagId) {
        return searchSoftwareId(tagId != null ? new HashSet<Long>(){{ add(tagId);}} : null);
    }

    @Override
    public HashSet<Long> searchSoftwareId(HashSet<Long> tagId) {
        StringBuilder keySb = new StringBuilder();
        for (Long id : tagId){
            keySb.append(id);
        }
        String key = StringUtils.md5(keySb.toString());
        HashSet<Long> ids = RedisUtils.getCache("tag_software",key,HashSet.class);
        if(ids == null || ids.size() == 0)
        {
            ids = softwareTagDao.searchSoftwareId(tagId);
            RedisUtils.setCache("tag_software",key,ids);
        }
        return ids;
    }
}
