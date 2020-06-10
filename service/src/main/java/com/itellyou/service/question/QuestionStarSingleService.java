package com.itellyou.service.question;

import com.itellyou.model.question.QuestionStarModel;

import java.util.HashSet;
import java.util.List;

public interface QuestionStarSingleService {

    QuestionStarModel find(Long questionId, Long userId);

    List<QuestionStarModel> search(HashSet<Long> questionIds, Long userId);
}
