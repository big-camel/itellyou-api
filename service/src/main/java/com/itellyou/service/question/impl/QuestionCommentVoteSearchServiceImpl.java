package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionCommentVoteDao;
import com.itellyou.model.question.QuestionCommentVoteModel;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.service.question.QuestionCommentVoteService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = "question_comment_vote")
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
    public List<QuestionCommentVoteModel> search(HashSet<Long> commentIds, Long userId) {
        return RedisUtils.fetchByCache("question_comment_vote_" + userId, QuestionCommentVoteModel.class,commentIds,(HashSet<Long> fetchIds) ->
                voteDao.search(fetchIds,userId)
                ,(QuestionCommentVoteModel voteModel, Long id) -> id != null && voteModel.cacheKey().equals(id.toString() + "-" + userId));
    }
}
