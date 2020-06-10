package com.itellyou.service.question;

import com.itellyou.model.question.QuestionTagModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface QuestionTagService {

    int add(QuestionTagModel model);

    int addAll(Long questionId, HashSet<Long> tagIds);

    int clear(Long questionId);

    int remove(Long questionId, Long tagId);

    Map<Long, List<QuestionTagModel>> searchTags(HashSet<Long> questionIds);

    HashSet<Long> searchTagId(Long questionId);

    HashSet<Long> searchQuestionId(Long tagId);

    HashSet<Long> searchQuestionId(HashSet<Long> tagId);
}
