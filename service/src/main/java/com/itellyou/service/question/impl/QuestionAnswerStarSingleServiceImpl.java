package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerStarDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionAnswerStarModel;
import com.itellyou.service.question.QuestionAnswerStarSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = CacheKeys.QUESTION_ANSWER_STAR_KEY)
@Service
public class QuestionAnswerStarSingleServiceImpl implements QuestionAnswerStarSingleService {

    private final QuestionAnswerStarDao starDao;

    public QuestionAnswerStarSingleServiceImpl(QuestionAnswerStarDao starDao) {
        this.starDao = starDao;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#answerId).concat('-').concat(#userId)",unless = "#result == null")
    public QuestionAnswerStarModel find(Long answerId, Long userId) {
        List<QuestionAnswerStarModel> starModels = starDao.search(answerId != null ? new HashSet<Long>(){{add(answerId);}} : null,userId,null,null,null,null,null,null);
        return starModels != null && starModels.size() > 0 ? starModels.get(0) : null;
    }

    @Override
    public List<QuestionAnswerStarModel> search(Collection<Long> answerIds, Long userId) {
        return RedisUtils.fetch(CacheKeys.QUESTION_ANSWER_STAR_KEY,QuestionAnswerStarModel.class,answerIds,(Collection<Long> fetchIds) ->
                starDao.search(fetchIds,userId,null,null,null,null,null,null),
                id -> id + "-" + userId,
                model -> model.cacheKey());
    }
}
