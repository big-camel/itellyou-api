package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerPaidReadModel;

import java.util.HashSet;
import java.util.List;

public interface QuestionAnswerPaidReadSearchService {

    QuestionAnswerPaidReadModel findByAnswerId(Long answerId);

    boolean checkRead(QuestionAnswerPaidReadModel paidReadModel,Long questionId, Long authorId, Long userId);

    List<QuestionAnswerPaidReadModel> search(HashSet<Long> answerIds);
}
