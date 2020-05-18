package com.itellyou.service.article;

import com.itellyou.model.article.ArticlePaidReadModel;

public interface ArticlePaidReadSearchService {

    ArticlePaidReadModel findByArticleId(Long articleId);

    boolean checkRead(ArticlePaidReadModel paidReadModel , Long authorId, Long userId);
}
