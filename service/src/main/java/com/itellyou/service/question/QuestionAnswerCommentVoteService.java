package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerCommentVoteModel;

import java.util.HashSet;
import java.util.List;

public interface QuestionAnswerCommentVoteService {

    List<QuestionAnswerCommentVoteModel> search(HashSet<Long> commentIds, Long userId);
}
