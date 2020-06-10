package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerStarModel;

import java.util.HashSet;
import java.util.List;

public interface QuestionAnswerStarSingleService {

    QuestionAnswerStarModel find(Long answerId, Long userId);

    List<QuestionAnswerStarModel> search(HashSet<Long> answerIds, Long userId);
}
