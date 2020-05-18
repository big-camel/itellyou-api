package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerVersionModel;

public interface QuestionAnswerVersionSearchService {

    QuestionAnswerVersionModel find(Long answerId, Integer version);
}
