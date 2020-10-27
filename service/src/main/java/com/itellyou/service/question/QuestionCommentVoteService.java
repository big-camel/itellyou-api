package com.itellyou.service.question;

import com.itellyou.model.question.QuestionCommentVoteModel;

import java.util.Collection;
import java.util.List;

public interface QuestionCommentVoteService {

    List<QuestionCommentVoteModel> search(Collection<Long> commentIds, Long userId);
}
