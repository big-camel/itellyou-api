package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface QuestionAnswerSingleService {
    QuestionAnswerModel findById(Long id);

    QuestionAnswerModel findByQuestionIdAndUserId(Long questionId, Long userId);

    List<QuestionAnswerModel> search(HashSet<Long> ids, HashSet<Long> questionId, String mode, Long searchUserId, Long userId,  Integer childCount, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long ip, Integer minComments, Integer maxComments,
                                     Integer minView, Integer maxView,
                                     Integer minSupport, Integer maxSupport,
                                     Integer minOppose, Integer maxOppose,
                                     Integer minStar, Integer maxStar,
                                     Long beginTime, Long endTime,
                                     Map<String, String> order,
                                     Integer offset,
                                     Integer limit);
}
