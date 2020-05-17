package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerPaidReadModel;

public interface QuestionAnswerPaidReadSearchService {

    QuestionAnswerPaidReadModel findByAnswerId(Long answerId);
}
