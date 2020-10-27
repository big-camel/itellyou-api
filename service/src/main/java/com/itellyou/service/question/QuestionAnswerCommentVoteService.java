package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerCommentVoteModel;

import java.util.Collection;
import java.util.List;

public interface QuestionAnswerCommentVoteService {

    List<QuestionAnswerCommentVoteModel> search(Collection<Long> commentIds, Long userId);
}
