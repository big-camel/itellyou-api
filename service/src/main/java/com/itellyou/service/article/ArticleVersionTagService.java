package com.itellyou.service.article;

import com.itellyou.model.article.ArticleVersionTagModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface ArticleVersionTagService {

    int add(ArticleVersionTagModel model);

    int addAll(Long versionId,HashSet<Long> tagIds);

    int clear(Long versionId);

    int remove(Long versionId,Long tagId);

    Map<Long, List<ArticleVersionTagModel>> searchTags(HashSet<Long> versionIds);

    HashSet<Long> searchTagId(Long versionId);
}
