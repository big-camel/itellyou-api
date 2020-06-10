package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerVoteModel;

import java.util.HashSet;
import java.util.List;

public interface QuestionAnswerVoteService {

    List<QuestionAnswerVoteModel> search(HashSet<Long> answerIds, Long userId);
}
