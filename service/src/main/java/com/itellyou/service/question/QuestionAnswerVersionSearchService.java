package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerVersionModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface QuestionAnswerVersionSearchService {

    Integer findVersionById(Long id);

    QuestionAnswerVersionModel find(Long answerId, Integer version);

    List<QuestionAnswerVersionModel> searchByAnswerId(Long answerId, Boolean hasContent);

    List<QuestionAnswerVersionModel> searchByAnswerId(Long answerId);

    List<QuestionAnswerVersionModel> searchByAnswerMap(Map<Long,Integer> articleMap, Boolean hasContent);

    QuestionAnswerVersionModel findById(Long id);

    QuestionAnswerVersionModel findByAnswerIdAndId(Long id, Long answerId);

    List<QuestionAnswerVersionModel> search( HashSet<Long> ids,  Map<Long,Integer> answerMap,
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

    Integer count ( HashSet<Long> ids,
                    Map<Long,Integer> answerMap,
                    Long userId,
                    Boolean isReview,
                    Boolean isDisable,
                    Boolean isPublish,
                    Long beginTime,
                    Long endTime,
                    Long ip);
}
