package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagInfoDao;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.service.tag.TagInfoService;
import com.itellyou.service.tag.TagVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@CacheConfig(cacheNames = "tag")
@Service
public class TagInfoServiceImpl implements TagInfoService {

    private final TagInfoDao tagInfoDao;

    @Autowired
    public TagInfoServiceImpl(TagInfoDao tagInfoDao,TagVersionService versionService){
        this.tagInfoDao = tagInfoDao;
    }

    @Override
    public int insert(TagInfoModel tagInfoModel) {
        return tagInfoDao.insert(tagInfoModel);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateStarCountById(Long id, Integer step) {
        return updateStarCountById(id != null ? new HashSet<Long>(){{ add(id);}} : null,step);
    }

    @Override
    public int updateStarCountById(HashSet<Long> ids, Integer step) {
        if(ids == null || ids.size() < 1) return 0;
        return tagInfoDao.updateStarCountById(ids,step);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateArticleCountById(Long id, Integer step) {
        return updateArticleCountById(id != null ? new HashSet<Long>(){{ add(id);}} : null,step);
    }

    @Override
    public int updateArticleCountById(HashSet<Long> ids, Integer step) {
        if(ids == null || ids.size() < 1) return 0;
        return tagInfoDao.updateArticleCountById(ids,step);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateQuestionCountById(Long id, Integer step) {
        return updateQuestionCountById(id != null ? new HashSet<Long>(){{ add(id);}} : null,step);
    }

    @Override
    public int updateQuestionCountById(HashSet<Long> ids, Integer step) {
        if(ids == null || ids.size() < 1) return 0;
        return tagInfoDao.updateQuestionCountById(ids,step);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int updateGroupByGroupId(Long nextGroupId, Long prevGroupId) {
        return tagInfoDao.updateGroupByGroupId(nextGroupId,prevGroupId);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateById(Long id,String name, Long groupId, Boolean isDisabled) {
        return tagInfoDao.updateById(id,name,groupId,isDisabled);
    }

    @Override
    public int updateInfo(Long id, String description, Long time, Long ip, Long userId) {
        return tagInfoDao.updateInfo(id,description,time,ip,userId);
    }
}
