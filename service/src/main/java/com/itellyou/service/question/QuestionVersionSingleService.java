package com.itellyou.service.question;

import com.itellyou.model.question.QuestionVersionModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface QuestionVersionSingleService {

    Integer findVersionById(Long id);

    List<QuestionVersionModel> searchByQuestionMap(Map<Long, Integer> questionMap, Boolean hasContent);

    QuestionVersionModel find(Long id);

    QuestionVersionModel find(Long questionId, Integer version);

    List<QuestionVersionModel> search(Collection<Long> ids,
                                      Map<Long, Integer> questionMap,
                                      Long userId,
                                      Boolean hasContent,
                                      Boolean isReview,
                                      Boolean isDisable,
                                      Boolean isPublish,
                                      Long beginTime,
                                      Long endTime,
                                      Long ip,
                                      Map<String, String> order,
                                      Integer offset,
                                      Integer limit);

    Integer count(Collection<Long> ids,
                  Map<Long, Integer> questionMap,
                  Long userId,
                  Boolean isReview,
                  Boolean isDisable,
                  Boolean isPublish,
                  Long beginTime,
                  Long endTime,
                  Long ip);
}
