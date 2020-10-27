package com.itellyou.service.article;

import com.itellyou.model.article.ArticleTagModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ArticleTagService {

    int add(ArticleTagModel model);

    int addAll(Long articleId,Collection<Long> tagIds);

    int clear(Long articleId);

    int remove(Long articleId,Long tagId);

    Map<Long, List<ArticleTagModel>> searchTags(Collection<Long> articleIds);

    Map<Long, List<ArticleTagModel>> searchArticles(Collection<Long> tagIds);

    Collection<Long> searchArticleIds(Collection<Long> tagIds);
}
