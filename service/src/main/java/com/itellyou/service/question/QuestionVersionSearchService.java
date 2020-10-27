package com.itellyou.service.question;

import com.itellyou.model.question.QuestionVersionDetailModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface QuestionVersionSearchService {

    List<QuestionVersionDetailModel> searchByQuestionId(Long questionId, Boolean hasContent);

    List<QuestionVersionDetailModel> searchByQuestionId(Long questionId);

    QuestionVersionDetailModel getDetail(Long questionId);

    QuestionVersionDetailModel getDetail(Long questionId, Integer version);

    List<QuestionVersionDetailModel> search(Collection<Long> ids,
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
}
