package com.itellyou.service.article;

import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface ArticleVersionSearchService {

    Integer findVersionById(Long id);

    List<ArticleVersionModel> searchByArticleId(Long articleId, Boolean hasContent);

    List<ArticleVersionModel> searchByArticleId(Long articleId);

    List<ArticleVersionModel> searchByArticleMap(Map<Long,Integer> articleMap,Boolean hasContent);

    ArticleVersionModel findById(Long id);

    ArticleVersionModel findByArticleIdAndId(Long id, Long articleId);

    ArticleVersionModel find(Long articleId,Integer version);

    List<ArticleVersionModel> search( HashSet<Long> ids,
                                      Map<Long,Integer> articleMap,
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

    Integer count ( HashSet<Long> ids,
                    Map<Long,Integer> articleMap,
                    Long userId,
                    ArticleSourceType sourceType,
                    Boolean isReview,
                    Boolean isDisable,
                    Boolean isPublish,
                    Long beginTime,
                    Long endTime,
                    Long ip);
}
