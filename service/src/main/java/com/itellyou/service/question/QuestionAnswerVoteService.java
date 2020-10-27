package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerVoteModel;

import java.util.Collection;
import java.util.List;

public interface QuestionAnswerVoteService {

    List<QuestionAnswerVoteModel> search(Collection<Long> answerIds, Long userId);
}
