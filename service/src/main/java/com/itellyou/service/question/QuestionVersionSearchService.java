package com.itellyou.service.question;

import com.itellyou.model.question.QuestionVersionModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface QuestionVersionSearchService {

    Integer findVersionById(Long id);

    List<QuestionVersionModel> searchByQuestionId(Long questionId, Boolean hasContent);

    List<QuestionVersionModel> searchByQuestionId(Long questionId);

    List<QuestionVersionModel> searchByQuestionMap(Map<Long,Integer> questionMap, Boolean hasContent);

    QuestionVersionModel findById(Long id);

    QuestionVersionModel findByQuestionIdAndId(Long id,Long questionId);

    List<QuestionVersionModel> search( HashSet<Long> ids,
                                       Map<Long,Integer> questionMap,
                                       Long userId,
                                       Boolean hasContent,
                                       Boolean isReview,
                                       Boolean isDisable,
                                       Boolean isPublish,
                                       Long beginTime,
                                       Long endTime,
                                       Long ip,
                                       Map<String,String> order,
                                       Integer offset,
                                       Integer limit);

    Integer count ( HashSet<Long> ids,
                    Map<Long,Integer> questionMap,
                    Long userId,
                    Boolean isReview,
                    Boolean isDisable,
                    Boolean isPublish,
                    Long beginTime,
                    Long endTime,
                    Long ip);
}
