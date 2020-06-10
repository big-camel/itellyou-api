package com.itellyou.service.question;

import com.itellyou.model.question.QuestionVersionTagModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface QuestionVersionTagService {

    int add(QuestionVersionTagModel model);

    int addAll(Long versionId, HashSet<Long> tagIds);

    int clear(Long versionId);

    int remove(Long versionId, Long tagId);

    Map<Long, List<QuestionVersionTagModel>> searchTags(HashSet<Long> versionIds);

    HashSet<Long> searchTagId(Long versionId);
}
