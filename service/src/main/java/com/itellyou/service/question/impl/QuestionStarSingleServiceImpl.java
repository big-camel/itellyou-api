package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionStarDao;
import com.itellyou.model.question.QuestionStarModel;
import com.itellyou.service.question.QuestionStarSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = "question_star")
@Service
public class QuestionStarSingleServiceImpl implements QuestionStarSingleService {

    private final QuestionStarDao starDao;

    public QuestionStarSingleServiceImpl(QuestionStarDao starDao) {
        this.starDao = starDao;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#questionId).concat('-').concat(#userId)",unless = "#result == null")
    public QuestionStarModel find(Long questionId, Long userId) {
        List<QuestionStarModel> starModels = starDao.search(questionId != null ? new HashSet<Long>(){{add(questionId);}} : null,userId,null,null,null,null,null,null);
        return starModels != null && starModels.size() > 0 ? starModels.get(0) : null;
    }

    @Override
    public List<QuestionStarModel> search(HashSet<Long> questionIds, Long userId) {
        return RedisUtils.fetchByCache("question_star_" + userId,QuestionStarModel.class,questionIds,(HashSet<Long> fetchIds) ->
                starDao.search(fetchIds,userId,null,null,null,null,null,null)
                ,(QuestionStarModel voteModel, Long id) -> id != null && voteModel.cacheKey().equals(id.toString() + "-" + userId));
    }
}
