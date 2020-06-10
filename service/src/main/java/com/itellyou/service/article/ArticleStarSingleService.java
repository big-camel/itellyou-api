package com.itellyou.service.article;

import com.itellyou.model.article.ArticleStarModel;

import java.util.HashSet;
import java.util.List;

public interface ArticleStarSingleService {

    ArticleStarModel find(Long articleId, Long userId);

    List<ArticleStarModel> search(HashSet<Long> articleIds, Long userId);
}
