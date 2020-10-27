package com.itellyou.service.question;

import com.itellyou.model.question.QuestionTagModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface QuestionTagService {

    int add(QuestionTagModel model);

    int addAll(Long questionId, Collection<Long> tagIds);

    int clear(Long questionId);

    int remove(Long questionId, Long tagId);

    Map<Long, List<QuestionTagModel>> searchTags(Collection<Long> questionIds);

    Map<Long, List<QuestionTagModel>> searchQuestions(Collection<Long> tagIds);

    Collection<Long> searchQuestionIds(Collection<Long> tagIds);


}
