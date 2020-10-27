package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagStarDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.tag.TagStarModel;
import com.itellyou.service.tag.TagStarSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = CacheKeys.TAG_STAR_KEY)
@Service
public class TagStarSingleServiceImpl implements TagStarSingleService {

    private final TagStarDao starDao;

    public TagStarSingleServiceImpl(TagStarDao starDao) {
        this.starDao = starDao;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#tagId).concat('-').concat(#userId)",unless = "#result == null")
    public TagStarModel find(Long tagId, Long userId) {
        List<TagStarModel> starModels = starDao.search(tagId != null ? new HashSet<Long>(){{add(tagId);}} : null,userId,null,null,null,null,null,null);
        return starModels != null && starModels.size() > 0 ? starModels.get(0) : null;
    }

    @Override
    public List<TagStarModel> search(Collection<Long> tagIds, Long userId) {
        return RedisUtils.fetch(CacheKeys.TAG_STAR_KEY + "_" + userId, TagStarModel.class,tagIds,(Collection<Long> fetchIds) ->
                starDao.search(fetchIds,userId,null,null,null,null,null,null),
                id -> id + "-" + userId,
                TagStarModel::cacheKey);
    }
}
