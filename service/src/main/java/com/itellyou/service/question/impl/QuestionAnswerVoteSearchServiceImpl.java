package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerVoteDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionAnswerVoteModel;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.service.question.QuestionAnswerVoteService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = CacheKeys.ANSWER_VOTE_KEY)
@Service
public class QuestionAnswerVoteSearchServiceImpl implements VoteSearchService<QuestionAnswerVoteModel> , QuestionAnswerVoteService {

    private final QuestionAnswerVoteDao voteDao;

    public QuestionAnswerVoteSearchServiceImpl(QuestionAnswerVoteDao voteDao) {
        this.voteDao = voteDao;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#answerId).concat('-').concat(#userId)",unless = "#result == null")
    public QuestionAnswerVoteModel findByTargetIdAndUserId(Long answerId, Long userId) {
        List<QuestionAnswerVoteModel> voteModels = voteDao.search(answerId != null ? new HashSet<Long>(){{ add(answerId);}} : null,userId);
        return voteModels != null && voteModels.size() > 0 ? voteModels.get(0) : null;
    }

    @Override
    public List<QuestionAnswerVoteModel> search(Collection<Long> answerIds, Long userId) {
        return RedisUtils.fetch(CacheKeys.ANSWER_VOTE_KEY, QuestionAnswerVoteModel.class,answerIds,(Collection<Long> fetchIds) ->
                voteDao.search(fetchIds,userId),
                id -> id + "-" + userId,
        QuestionAnswerVoteModel::cacheKey);
    }
}
