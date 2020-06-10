package com.itellyou.service.article;

import com.itellyou.model.article.ArticleTagModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface ArticleTagService {

    int add(ArticleTagModel model);

    int addAll(Long articleId,HashSet<Long> tagIds);

    int clear(Long articleId);

    int remove(Long articleId,Long tagId);

    Map<Long, List<ArticleTagModel>> searchTags(HashSet<Long> articleIds);

    HashSet<Long> searchTagId(Long articleId);

    HashSet<Long> searchArticleId(Long tagId);

    HashSet<Long> searchArticleId(HashSet<Long> tagId);
}
