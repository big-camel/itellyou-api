package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerStarDao;
import com.itellyou.model.question.QuestionAnswerStarModel;
import com.itellyou.service.question.QuestionAnswerStarSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = "question_answer_star")
@Service
public class QuestionAnswerStarSingleServiceImpl implements QuestionAnswerStarSingleService {

    private final QuestionAnswerStarDao starDao;

    public QuestionAnswerStarSingleServiceImpl(QuestionAnswerStarDao starDao) {
        this.starDao = starDao;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#columnId).concat('-').concat(#userId)",unless = "#result == null")
    public QuestionAnswerStarModel find(Long answerId, Long userId) {
        List<QuestionAnswerStarModel> starModels = starDao.search(answerId != null ? new HashSet<Long>(){{add(answerId);}} : null,userId,null,null,null,null,null,null);
        return starModels != null && starModels.size() > 0 ? starModels.get(0) : null;
    }

    @Override
    public List<QuestionAnswerStarModel> search(HashSet<Long> answerIds, Long userId) {
        return RedisUtils.fetchByCache("question_answer_star_" + userId,QuestionAnswerStarModel.class,answerIds,(HashSet<Long> fetchIds) ->
                starDao.search(fetchIds,userId,null,null,null,null,null,null)
                ,(QuestionAnswerStarModel voteModel, Long id) -> id != null && voteModel.cacheKey().equals(id.toString() + "-" + userId));
    }
}
