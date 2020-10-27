package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerTotalModel;
import com.itellyou.model.sys.PageModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface QuestionAnswerSingleService {
    QuestionAnswerModel findById(Long id);

    QuestionAnswerModel findByQuestionIdAndUserId(Long questionId, Long userId,String mode);

    List<QuestionAnswerModel> search(Collection<Long> ids, Collection<Long> questionId, String mode, Long userId, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long ip, Integer minComment, Integer maxComment,
                                     Integer minView, Integer maxView,
                                     Integer minSupport, Integer maxSupport,
                                     Integer minOppose, Integer maxOppose,
                                     Integer minStar, Integer maxStar,
                                     Long beginTime, Long endTime,
                                     Map<String, String> order,
                                     Integer offset,
                                     Integer limit);

    int count(Collection<Long> ids, Collection<Long> questionIds, String mode, Long userId, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long ip, Integer minComment, Integer maxComment,
              Integer minView, Integer maxView,
              Integer minSupport, Integer maxSupport,
              Integer minOppose, Integer maxOppose,
              Integer minStar, Integer maxStar,
              Long beginTime, Long endTime);

    PageModel<QuestionAnswerModel> page(Collection<Long> ids, Collection<Long> questionIds, String mode, Long userId, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long ip, Integer minComment, Integer maxComment,
                                        Integer minView, Integer maxView,
                                        Integer minSupport, Integer maxSupport,
                                        Integer minOppose, Integer maxOppose,
                                        Integer minStar, Integer maxStar,
                                        Long beginTime, Long endTime,
                                        Map<String, String> order,
                                        Integer offset,
                                        Integer limit);

    List<QuestionAnswerTotalModel> totalByUser(Collection<Long> userIds, Long questionId, Boolean isAdopted,
                                               Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long beginTime, Long endTime,
                                               Map<String, String> order,
                                               Integer offset,
                                               Integer limit);
}
