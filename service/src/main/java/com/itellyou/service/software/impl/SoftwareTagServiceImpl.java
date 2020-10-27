package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareTagDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.software.SoftwareTagModel;
import com.itellyou.service.software.SoftwareTagService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = CacheKeys.SOFTWARE_TAG_KEY)
public class SoftwareTagServiceImpl implements SoftwareTagService {

    private final SoftwareTagDao softwareTagDao;

    public SoftwareTagServiceImpl(SoftwareTagDao softwareTagDao) {
        this.softwareTagDao = softwareTagDao;
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#model.softwareId") , @CacheEvict(value = CacheKeys.TAG_SOFTWARE_KEY , key = "#model.tagId")})
    public int add(SoftwareTagModel model) {
        return softwareTagDao.add(model);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#softwareId") })
    public int addAll(Long softwareId, Collection<Long> tagIds) {
        tagIds.forEach((Long id) -> RedisUtils.remove(CacheKeys.TAG_SOFTWARE_KEY,id));
        return softwareTagDao.addAll(softwareId,tagIds);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#softwareId") })
    public int clear(Long softwareId) {
        Map<Long, List<SoftwareTagModel>> tags = searchTags(new HashSet<Long>(){{add(softwareId);}});
        tags.values().forEach((List<SoftwareTagModel> models) ->
                models.forEach((SoftwareTagModel model) -> RedisUtils.remove(CacheKeys.TAG_SOFTWARE_KEY,model.getTagId()))
        );
        return softwareTagDao.clear(softwareId);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#softwareId") , @CacheEvict(value = CacheKeys.TAG_SOFTWARE_KEY , key = "#tagId" )})
    public int remove(Long softwareId, Long tagId) {
        return softwareTagDao.remove(softwareId,tagId);
    }

    @Override
    public Map<Long, List<SoftwareTagModel>> searchTags(Collection<Long> softwareIds) {
        return RedisUtils.fetch(CacheKeys.SOFTWARE_TAG_KEY,softwareIds,
                fetchIds -> softwareTagDao.searchTags(fetchIds),
                SoftwareTagModel::getSoftwareId);
    }

    @Override
    public Map<Long, List<SoftwareTagModel>> searchSoftwares(Collection<Long> tagIds) {
        return RedisUtils.fetch(CacheKeys.TAG_SOFTWARE_KEY,tagIds,
                fetchIds -> softwareTagDao.searchSoftwares(fetchIds),
                SoftwareTagModel::getTagId);
    }

    @Override
    public Collection<Long> searchSoftwareIds(Collection<Long> tagIds) {
        Map<Long, List<SoftwareTagModel>> list = searchSoftwares(tagIds);
        Collection<Long> ids = new LinkedHashSet<>();
        for (Long id : list.keySet()){
            ids.addAll(list.get(id).stream().map(SoftwareTagModel::getSoftwareId).collect(Collectors.toSet()));
        }
        return ids;
    }
}
