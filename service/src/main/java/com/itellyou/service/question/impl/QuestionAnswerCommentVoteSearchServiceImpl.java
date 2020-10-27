package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerCommentVoteDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionAnswerCommentVoteModel;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.service.question.QuestionAnswerCommentVoteService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = CacheKeys.ANSWER_COMMENT_VOTE_KEY)
@Service
public class QuestionAnswerCommentVoteSearchServiceImpl implements VoteSearchService<QuestionAnswerCommentVoteModel> , QuestionAnswerCommentVoteService {

    private final QuestionAnswerCommentVoteDao voteDao;

    public QuestionAnswerCommentVoteSearchServiceImpl(QuestionAnswerCommentVoteDao voteDao) {
        this.voteDao = voteDao;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#commentId).concat('-').concat(#userId)",unless = "#result == null")
    public QuestionAnswerCommentVoteModel findByTargetIdAndUserId(Long commentId, Long userId) {
        List<QuestionAnswerCommentVoteModel> voteModels = voteDao.search(commentId != null ? new HashSet<Long>(){{ add(commentId);}} : null,userId);
        return voteModels != null && voteModels.size() > 0 ? voteModels.get(0) : null;
    }

    @Override
    public List<QuestionAnswerCommentVoteModel> search(Collection<Long> commentIds, Long userId) {
        return RedisUtils.fetch(CacheKeys.ANSWER_VOTE_KEY, QuestionAnswerCommentVoteModel.class,commentIds,(Collection<Long> fetchIds) ->
                voteDao.search(fetchIds,userId),
                id -> id + "-" + userId,
                model -> model.cacheKey());
    }
}
