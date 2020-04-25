package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerCommentVoteModel;
import com.itellyou.model.question.QuestionAnswerVoteModel;

public interface QuestionAnswerCommentVoteService {
    int insert(QuestionAnswerCommentVoteModel voteModel);

    int deleteByCommentIdAndUserId(Long commentId, Long userId);

    QuestionAnswerCommentVoteModel findByCommentIdAndUserId(Long commentId, Long userId);
}
