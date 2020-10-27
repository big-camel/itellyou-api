package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareVersionTagDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.software.SoftwareVersionTagModel;
import com.itellyou.service.software.SoftwareVersionTagService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@CacheConfig(cacheNames = CacheKeys.SOFTWARE_VERSION_TAG_KEY)
public class SoftwareVersionTagServiceImpl implements SoftwareVersionTagService {

    private final SoftwareVersionTagDao versionTagDao;

    public SoftwareVersionTagServiceImpl(SoftwareVersionTagDao versionTagDao) {
        this.versionTagDao = versionTagDao;
    }

    @Override
    @CacheEvict(key = "#model.version")
    public int add(SoftwareVersionTagModel model) {
        return versionTagDao.add(model);
    }

    @Override
    @CacheEvict(key = "#versionId")
    public int addAll(Long versionId, Collection<Long> tagIds) {
        return versionTagDao.addAll(versionId,tagIds);
    }

    @Override
    @CacheEvict
    public int clear(Long versionId) {
        return versionTagDao.clear(versionId);
    }

    @Override
    @CacheEvict(key = "#versionId")
    public int remove(Long versionId, Long tagId) {
        return versionTagDao.remove(versionId,tagId);
    }

    @Override
    public Map<Long, List<SoftwareVersionTagModel>> searchTags(Collection<Long> versionIds) {
        List<SoftwareVersionTagModel> models = versionTagDao.searchTags(versionIds);
        Map<Long, List<SoftwareVersionTagModel>> map = new LinkedHashMap<>();
        for (SoftwareVersionTagModel model : models){
            if(!map.containsKey(model.getVersionId())){
                map.put(model.getVersionId(),new LinkedList<>());
            }
            map.get(model.getVersionId()).add(model);
        }
        return map;
    }
}
