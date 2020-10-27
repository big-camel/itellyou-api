package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerVersionDetailModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface QuestionAnswerVersionSearchService {

    List<QuestionAnswerVersionDetailModel> searchByAnswerId(Long answerId, Boolean hasContent);

    List<QuestionAnswerVersionDetailModel> searchByAnswerId(Long answerId);

    QuestionAnswerVersionDetailModel getDetail(Long id);

    QuestionAnswerVersionDetailModel getDetail(Long answerId, Integer version);

    List<QuestionAnswerVersionDetailModel> search(Collection<Long> ids, Map<Long,Integer> answerMap,
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
}
