package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionVersionTagDao;
import com.itellyou.model.question.QuestionVersionTagModel;
import com.itellyou.service.question.QuestionVersionTagService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@CacheConfig(cacheNames = "question_version_tag")
public class QuestionVersionTagServiceImpl implements QuestionVersionTagService {

    private final QuestionVersionTagDao versionTagDao;

    public QuestionVersionTagServiceImpl(QuestionVersionTagDao versionTagDao) {
        this.versionTagDao = versionTagDao;
    }

    @Override
    @CacheEvict(key = "#model.version")
    public int add(QuestionVersionTagModel model) {
        return versionTagDao.add(model);
    }

    @Override
    @CacheEvict(key = "#versionId")
    public int addAll(Long versionId, HashSet<Long> tagIds) {
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
    public Map<Long, List<QuestionVersionTagModel>> searchTags(HashSet<Long> versionIds) {
        List<QuestionVersionTagModel> models = versionTagDao.searchTags(versionIds);
        Map<Long, List<QuestionVersionTagModel>> map = new LinkedHashMap<>();
        for (QuestionVersionTagModel model : models){
            if(!map.containsKey(model.getVersionId())){
                map.put(model.getVersionId(),new LinkedList<>());
            }
            map.get(model.getVersionId()).add(model);
        }
        return map;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public HashSet<Long> searchTagId(Long versionId) {
        return versionTagDao.searchTagId(versionId);
    }
}
