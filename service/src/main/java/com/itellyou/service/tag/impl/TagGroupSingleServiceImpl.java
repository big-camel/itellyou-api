package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagGroupDao;
import com.itellyou.model.tag.TagGroupModel;
import com.itellyou.service.tag.TagGroupSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "tag_group")
@Service
public class TagGroupSingleServiceImpl implements TagGroupSingleService {

    private final TagGroupDao groupDao;

    public TagGroupSingleServiceImpl(TagGroupDao groupDao) {
        this.groupDao = groupDao;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public TagGroupModel findById(Long id) {
        return groupDao.findById(id);
    }

    @Override
    public TagGroupModel findByName(String name) {
        return groupDao.findByName(name);
    }

    @Override
    public List<TagGroupModel> search(HashSet<Long> ids, Long userId, Long ip, Boolean isDisabled, Boolean isPublished, Integer minTagCount, Integer maxTagCount, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetchByCache("tag_group",TagGroupModel.class,ids, (HashSet<Long> fetchIds) ->
                groupDao.search(fetchIds,userId,ip,isDisabled,isPublished,minTagCount,maxTagCount,beginTime,endTime,order,offset,limit)
        );
    }
}
