package com.itellyou.service.article;

import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ArticleVersionSingleService {

    Integer findVersionById(Long id);

    List<ArticleVersionModel> searchByArticleMap(Map<Long, Integer> articleMap, Boolean hasContent);

    ArticleVersionModel find(Long id);

    ArticleVersionModel find(Long articleId, Integer version);

    List<ArticleVersionModel> search(Collection<Long> ids,
                                     Map<Long, Integer> articleMap,
                                     Long userId,
                                     ArticleSourceType sourceType,
                                     Boolean hasContent,
                                     Boolean isReview,
                                     Boolean isDisable,
                                     Boolean isPublish,
                                     Long beginTime,
                                     Long endTime,
                                     Long ip,
                                     Map<String, String> order,
                                     Integer offset,
                                     Integer limit);

    Integer count(Collection<Long> ids,
                  Map<Long, Integer> articleMap,
                  Long userId,
                  ArticleSourceType sourceType,
                  Boolean isReview,
                  Boolean isDisable,
                  Boolean isPublish,
                  Long beginTime,
                  Long endTime,
                  Long ip);
}
