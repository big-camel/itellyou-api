package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerCommentVoteDao;
import com.itellyou.model.question.QuestionAnswerCommentVoteModel;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.service.question.QuestionAnswerCommentVoteService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = "answer_comment_vote")
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
    public List<QuestionAnswerCommentVoteModel> search(HashSet<Long> commentIds, Long userId) {
        return RedisUtils.fetchByCache("answer_vote_" + userId, QuestionAnswerCommentVoteModel.class,commentIds,(HashSet<Long> fetchIds) ->
                voteDao.search(fetchIds,userId)
                ,(QuestionAnswerCommentVoteModel voteModel, Long id) -> id != null && voteModel.cacheKey().equals(id.toString() + "-" + userId));
    }
}
