package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagVersionDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.tag.TagVersionModel;
import com.itellyou.service.tag.TagVersionSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = CacheKeys.TAG_VERSION_KEY)
@Service
public class TagVersionSingleServiceImpl implements TagVersionSingleService {

    private final TagVersionDao versionDao;

    public TagVersionSingleServiceImpl(TagVersionDao versionDao) {
        this.versionDao = versionDao;
    }

    @Override
    public Integer findVersionById(Long id) {
        return versionDao.findVersionById(id);
    }

    @Override
    public List<TagVersionModel> searchByTagMap(Map<Long, Integer> tagMap, Boolean hasContent) {
        return search(null,tagMap,null,hasContent,null,null,null,null,null,null,null,null,null);
    }

    @Override
    public List<TagVersionModel> search(Collection<Long> ids, Map<Long, Integer> tagMap, Long userId, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<TagVersionModel> versionModels = RedisUtils.fetch(CacheKeys.TAG_VERSION_KEY, TagVersionModel.class,ids,(Collection<Long> fetchIds) ->
                versionDao.search(fetchIds,tagMap,userId,hasContent,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit),
                id -> id + (hasContent == null || hasContent == true ? "" : "-nc"),
                // 如果没有传ids，那么使用默认cacheKey缓存数据
                model -> (ids != null && ids.size() > 0 ? model.getId() : model.cacheKey()) + (hasContent == null || hasContent == true ? "" : "-nc")
        );
        return  versionModels;
    }

    @Override
    public int count(Collection<Long> ids,Map<Long,Integer> tagMap, Long userId, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip) {
        return versionDao.count(ids,tagMap,userId,isReview,isDisable,isPublish,beginTime,endTime,ip);
    }

    @Override
    public TagVersionModel find(Long id) {
        List<TagVersionModel> list = search(id != null ? new HashSet<Long>(){{add(id);}} : null, null, null, true, null, null, null, null, null, null, null, null, null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }

    @Override
    public TagVersionModel find(Long tagId, Integer version) {
        List<TagVersionModel> list = search(null, new HashMap<Long, Integer>(){{put(tagId,version);}}, null, true, null, null, null, null, null, null, null, null, null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }
}
