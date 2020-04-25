package com.itellyou.service.question;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionStarDetailModel;
import com.itellyou.model.question.QuestionStarModel;

import java.util.List;
import java.util.Map;

public interface QuestionStarService {
    int insert(QuestionStarModel model) throws Exception;
    int delete(Long questionId, Long userId) throws Exception;
    List<QuestionStarDetailModel> search(Long questionId, Long userId,
                                         Long beginTime, Long endTime,
                                         Long ip,
                                         Map<String, String> order,
                                         Integer offset,
                                         Integer limit);
    int count(Long questionId, Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<QuestionStarDetailModel> page(Long questionId, Long userId,
                                          Long beginTime, Long endTime,
                                          Long ip,
                                          Map<String, String> order,
                                          Integer offset,
                                          Integer limit);
}
