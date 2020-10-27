package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerVersionModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface QuestionAnswerVersionSingleService {

    Integer findVersionById(Long id);

    QuestionAnswerVersionModel find(Long answerId, Integer version);

    List<QuestionAnswerVersionModel> searchByAnswerMap(Map<Long, Integer> articleMap, Boolean hasContent);

    QuestionAnswerVersionModel find(Long id);

    List<QuestionAnswerVersionModel> search(Collection<Long> ids, Map<Long, Integer> answerMap,
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
                  Map<Long, Integer> answerMap,
                  Long userId,
                  Boolean isReview,
                  Boolean isDisable,
                  Boolean isPublish,
                  Long beginTime,
                  Long endTime,
                  Long ip);
}
