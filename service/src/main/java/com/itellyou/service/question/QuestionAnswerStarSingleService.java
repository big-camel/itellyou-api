package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerStarModel;

import java.util.Collection;
import java.util.List;

public interface QuestionAnswerStarSingleService {

    QuestionAnswerStarModel find(Long answerId, Long userId);

    List<QuestionAnswerStarModel> search(Collection<Long> answerIds, Long userId);
}
