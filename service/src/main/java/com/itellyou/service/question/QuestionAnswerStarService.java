package com.itellyou.service.question;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionAnswerStarDetailModel;
import com.itellyou.model.question.QuestionAnswerStarModel;

import java.util.List;
import java.util.Map;

public interface QuestionAnswerStarService {
    int insert(QuestionAnswerStarModel model) throws Exception;
    int delete(Long answerId, Long userId) throws Exception;
    List<QuestionAnswerStarDetailModel> search(Long answerId, Long userId,
                                         Long beginTime, Long endTime,
                                         Long ip,
                                         Map<String, String> order,
                                         Integer offset,
                                         Integer limit);
    int count(Long answerId, Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<QuestionAnswerStarDetailModel> page(Long answerId, Long userId,
                                            Long beginTime, Long endTime,
                                            Long ip,
                                            Map<String, String> order,
                                            Integer offset,
                                            Integer limit);
}
