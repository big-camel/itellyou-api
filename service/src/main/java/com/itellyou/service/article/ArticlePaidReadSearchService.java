package com.itellyou.service.article;

import com.itellyou.model.article.ArticlePaidReadModel;

import java.util.Collection;
import java.util.List;

public interface ArticlePaidReadSearchService {

    ArticlePaidReadModel findByArticleId(Long articleId);

    boolean checkRead(ArticlePaidReadModel paidReadModel , Long authorId, Long userId);

    List<ArticlePaidReadModel> search(Collection<Long> articleIds);
}
