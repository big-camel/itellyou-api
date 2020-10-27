package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerVersionDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionAnswerVersionModel;
import com.itellyou.service.question.QuestionAnswerVersionSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = CacheKeys.QUESTION_ANSWER_VERSION_KEY)
@Service
public class QuestionAnswerVersionSingleServiceImpl implements QuestionAnswerVersionSingleService {

    private final QuestionAnswerVersionDao versionDao;

    public QuestionAnswerVersionSingleServiceImpl(QuestionAnswerVersionDao versionDao) {
        this.versionDao = versionDao;
    }

    @Override
    public Integer findVersionById(Long id) {
        return versionDao.findVersionById(id);
    }

    @Override
    public QuestionAnswerVersionModel find(Long answerId, Integer version) {
        List<QuestionAnswerVersionModel> list = search(null, new HashMap<Long, Integer>(){{put(answerId,version);}}, null, null, true, null, null, null, null, null, null, null, null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }

    @Override
    public List<QuestionAnswerVersionModel> searchByAnswerMap(Map<Long, Integer> answerMap, Boolean hasContent) {
        return RedisUtils.fetch(CacheKeys.QUESTION_ANSWER_VERSION_KEY, QuestionAnswerVersionModel.class, answerMap, (Map<Long, Integer> fetchMap) ->
                        search(null, answerMap, null, hasContent, null, null, null, null, null, null, null, null, null),
                (key, value) -> key + "-" + value + (hasContent == null || hasContent == true ? "" : "-nc"),
                model -> model.cacheKey() + (hasContent == null || hasContent == true ? "" : "-nc"));
    }

    @Override
    @Cacheable(unless = "#result == null")
    public QuestionAnswerVersionModel find(Long id) {
        List<QuestionAnswerVersionModel> list = search(id != null ? new HashSet<Long>(){{add(id);}} : null, null, null, null, true, null, null, null, null, null, null, null, null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }

    @Override
    public List<QuestionAnswerVersionModel> search(Collection<Long> ids, Map<Long, Integer> answerMap, Long userId, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionAnswerVersionModel> versionModels = RedisUtils.fetch(CacheKeys.QUESTION_ANSWER_VERSION_KEY, QuestionAnswerVersionModel.class,ids,(Collection<Long> fetchIds) ->
                versionDao.search(fetchIds,answerMap,userId,hasContent,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit),
                id -> id + (hasContent == null || hasContent == true ? "" : "-nc"),
                // 如果没有传ids，那么使用默认cacheKey缓存数据
                model -> (ids != null && ids.size() > 0 ? model.getId() : model.cacheKey()) + (hasContent == null || hasContent == true ? "" : "-nc")
        );
        return versionModels;
    }

    @Override
    public Integer count(Collection<Long> ids, Map<Long, Integer> answerMap, Long userId, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip) {
        return versionDao.count(ids,answerMap,userId,isReview,isDisable,isPublish,beginTime,endTime,ip);
    }
}
