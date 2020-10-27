package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionTagDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionTagModel;
import com.itellyou.service.question.QuestionTagService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = CacheKeys.QUESTION_TAG_KEY)
public class QuestionTagServiceImpl implements QuestionTagService {

    private final QuestionTagDao questionTagDao;

    public QuestionTagServiceImpl(QuestionTagDao questionTagDao) {
        this.questionTagDao = questionTagDao;
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#model.questionId") , @CacheEvict(value = CacheKeys.TAG_QUESTION_KEY , key = "#model.tagId")})
    public int add(QuestionTagModel model) {
        return questionTagDao.add(model);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#questionId") })
    public int addAll(Long questionId, Collection<Long> tagIds) {
        tagIds.forEach((Long id) -> RedisUtils.remove(CacheKeys.TAG_QUESTION_KEY,id));
        return questionTagDao.addAll(questionId,tagIds);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#questionId") })
    public int clear(Long questionId) {
        Map<Long, List<QuestionTagModel>> tags = searchTags(new HashSet<Long>(){{add(questionId);}});
        tags.values().forEach((List<QuestionTagModel> models) ->
                models.forEach((QuestionTagModel model) -> RedisUtils.remove(CacheKeys.TAG_QUESTION_KEY,model.getTagId()))
        );
        return questionTagDao.clear(questionId);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#questionId") , @CacheEvict(value = CacheKeys.TAG_QUESTION_KEY , key = "#tagId")})
    public int remove(Long questionId, Long tagId) {
        return questionTagDao.remove(questionId,tagId);
    }

    @Override
    public Map<Long, List<QuestionTagModel>> searchTags(Collection<Long> questionIds) {
        return RedisUtils.fetch(CacheKeys.QUESTION_TAG_KEY,questionIds,
                fetchIds -> questionTagDao.searchTags(fetchIds),
                QuestionTagModel::getQuestionId);
    }

    @Override
    public Map<Long, List<QuestionTagModel>> searchQuestions(Collection<Long> tagIds) {
        return RedisUtils.fetch(CacheKeys.TAG_QUESTION_KEY,tagIds,
                fetchIds -> questionTagDao.searchQuestions(fetchIds),
                QuestionTagModel::getTagId);
    }

    @Override
    public Collection<Long> searchQuestionIds(Collection<Long> tagIds) {
        Map<Long, List<QuestionTagModel>> list = searchQuestions(tagIds);
        Collection<Long> ids = new LinkedHashSet<>();
        for (Long id : list.keySet()){
            ids.addAll(list.get(id).stream().map(QuestionTagModel::getQuestionId).collect(Collectors.toSet()));
        }
        return ids;
    }
}
