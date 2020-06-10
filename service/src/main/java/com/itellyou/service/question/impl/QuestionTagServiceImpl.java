package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionTagDao;
import com.itellyou.model.question.QuestionTagModel;
import com.itellyou.service.question.QuestionTagService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@CacheConfig(cacheNames = "question_tag")
public class QuestionTagServiceImpl implements QuestionTagService {

    private final QuestionTagDao questionTagDao;

    public QuestionTagServiceImpl(QuestionTagDao questionTagDao) {
        this.questionTagDao = questionTagDao;
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#model.questionId") , @CacheEvict(value = "tag_question" , allEntries = true)})
    public int add(QuestionTagModel model) {
        return questionTagDao.add(model);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#questionId") , @CacheEvict(value = "tag_question" , allEntries = true)})
    public int addAll(Long questionId, HashSet<Long> tagIds) {
        return questionTagDao.addAll(questionId,tagIds);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#questionId") , @CacheEvict(value = "tag_question" , allEntries = true)})
    public int clear(Long questionId) {
        return questionTagDao.clear(questionId);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#questionId") , @CacheEvict(value = "tag_question" , allEntries = true)})
    public int remove(Long questionId, Long tagId) {
        return questionTagDao.remove(questionId,tagId);
    }

    @Override
    public Map<Long, List<QuestionTagModel>> searchTags(HashSet<Long> questionIds) {
        List<QuestionTagModel> models = questionTagDao.searchTags(questionIds);
        Map<Long, List<QuestionTagModel>> map = new LinkedHashMap<>();
        for (QuestionTagModel model : models){
            if(!map.containsKey(model.getQuestionId())){
                map.put(model.getQuestionId(),new LinkedList<>());
            }
            map.get(model.getQuestionId()).add(model);
        }
        return map;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public HashSet<Long> searchTagId(Long questionId) {
        return questionTagDao.searchTagId(questionId);
    }

    @Override
    @Cacheable(value = "tag_question",key = "#tagId",unless = "#result == null")
    public HashSet<Long> searchQuestionId(Long tagId) {
        return searchQuestionId(tagId != null ? new HashSet<Long>(){{ add(tagId);}} : null);
    }

    @Override
    @Cacheable(value = "tag_question",unless = "#result == null")
    public HashSet<Long> searchQuestionId(HashSet<Long> tagId) {
        return questionTagDao.searchQuestionId(tagId);
    }
}
