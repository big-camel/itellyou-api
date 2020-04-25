package com.itellyou.service.question;

import com.itellyou.model.question.QuestionCommentVoteModel;

public interface QuestionCommentVoteService {
    int insert(QuestionCommentVoteModel voteModel);

    int deleteByCommentIdAndUserId(Long commentId, Long userId);

    QuestionCommentVoteModel findByCommentIdAndUserId(Long commentId, Long userId);
}
