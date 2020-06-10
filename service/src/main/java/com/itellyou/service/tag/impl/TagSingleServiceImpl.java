package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagInfoDao;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.service.tag.TagSingleService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig( cacheNames = "tag" )
public class TagSingleServiceImpl implements TagSingleService {

    private final TagInfoDao tagInfoDao;

    public TagSingleServiceImpl(TagInfoDao tagInfoDao) {
        this.tagInfoDao = tagInfoDao;
    }

    @Override
    public int exists(List<Long> ids) {
        Long[] idArray = new Long[ids.size()];
        ids.toArray(idArray);
        return exists(idArray);
    }

    @Override
    public int exists(Long... ids) {
        return tagInfoDao.exists(ids);
    }

    @Override
    @Cacheable(key = "#id" , unless = "#result == null")
    public TagInfoModel findById(Long id) {
        return tagInfoDao.findById(id);
    }

    @Override
    public TagInfoModel findByName(String name) {
        return tagInfoDao.findByName(name);
    }
}
