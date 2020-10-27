package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerPaidReadModel;

import java.util.Collection;
import java.util.List;

public interface QuestionAnswerPaidReadSearchService {

    QuestionAnswerPaidReadModel findByAnswerId(Long answerId);

    boolean checkRead(QuestionAnswerPaidReadModel paidReadModel,Long questionId, Long authorId, Long userId);

    List<QuestionAnswerPaidReadModel> search(Collection<Long> answerIds);
}
