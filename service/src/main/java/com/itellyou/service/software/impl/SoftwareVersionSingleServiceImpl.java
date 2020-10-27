package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareVersionDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.software.SoftwareVersionModel;
import com.itellyou.service.software.SoftwareVersionSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = CacheKeys.SOFTWARE_VERSION_KEY)
@Service
public class SoftwareVersionSingleServiceImpl implements SoftwareVersionSingleService {

    private final SoftwareVersionDao versionDao;

    public SoftwareVersionSingleServiceImpl(SoftwareVersionDao versionDao) {
        this.versionDao = versionDao;
    }

    @Override
    public SoftwareVersionModel find(Long softwareId, Integer version) {
        List<SoftwareVersionModel> list = search(null, new HashMap<Long, Integer>(){{put(softwareId,version);}}, null, null, true, null, null, null, null, null, null, null, null, null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }

    @Override
    public List<SoftwareVersionModel> search(Collection<Long> ids, Map<Long, Integer> softwareMap, Long userId,Long groupId, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<SoftwareVersionModel> versionModels = RedisUtils.fetch(CacheKeys.SOFTWARE_VERSION_KEY, SoftwareVersionModel.class,ids,(Collection<Long> fetchIds) ->
                versionDao.search(fetchIds,softwareMap,userId,groupId,hasContent,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit),
                id -> id + (hasContent == null || hasContent == true ? "" : "-nc"),
                // 如果没有传ids，那么使用默认cacheKey缓存数据
                model -> (ids != null && ids.size() > 0 ? model.getId() : model.cacheKey()) + (hasContent == null || hasContent == true ? "" : "-nc")
        );
        return  versionModels;
    }

    @Override
    public Integer count(Collection<Long> ids, Map<Long, Integer> softwareMap, Long userId,Long groupId,  Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip) {
        return versionDao.count(ids,softwareMap,userId,groupId,isReview,isDisable,isPublish,beginTime,endTime,ip);
    }

    @Override
    public Integer findVersionById(Long id) {
        return versionDao.findVersionById(id);
    }

    @Override
    public List<SoftwareVersionModel> searchBySoftwareMap(Map<Long, Integer> softwareMap, Boolean hasContent) {
        return search(null,softwareMap,null,null,hasContent,null,null,null,null,null,null,null,null,null);
    }

    @Override
    public SoftwareVersionModel find(Long id) {
        List<SoftwareVersionModel> list = search(id != null ? new HashSet<Long>(){{add(id);}} : null, null, null, null, true, null, null, null, null, null, null, null, null, null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }
}
