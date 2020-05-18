package com.itellyou.service.article;

import com.itellyou.model.article.ArticleVersionModel;

public interface ArticleVersionSearchService {

    ArticleVersionModel find(Long articleId,Integer version);
}
