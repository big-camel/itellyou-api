package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionCommentVoteDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionCommentVoteModel;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.service.question.QuestionCommentVoteService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = CacheKeys.QUESTION_COMMENT_VOTE_KEY)
@Service
public class QuestionCommentVoteSearchServiceImpl implements VoteSearchService<QuestionCommentVoteModel> , QuestionCommentVoteService {

    private final QuestionCommentVoteDao voteDao;

    public QuestionCommentVoteSearchServiceImpl(QuestionCommentVoteDao voteDao) {
        this.voteDao = voteDao;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#commentId).concat('-').concat(#userId)",unless = "#result == null")
    public QuestionCommentVoteModel findByTargetIdAndUserId(Long commentId, Long userId) {
        List<QuestionCommentVoteModel> voteModels = voteDao.search(commentId != null ? new HashSet<Long>(){{ add(commentId);}} : null,userId);
        return voteModels != null && voteModels.size() > 0 ? voteModels.get(0) : null;
    }

    @Override
    public List<QuestionCommentVoteModel> search(Collection<Long> commentIds, Long userId) {
        return RedisUtils.fetch(CacheKeys.QUESTION_COMMENT_VOTE_KEY, QuestionCommentVoteModel.class,commentIds,(Collection<Long> fetchIds) ->
                voteDao.search(fetchIds,userId),
                id -> id + "-" + userId,
                QuestionCommentVoteModel::cacheKey);
    }
}
