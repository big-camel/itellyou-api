package com.itellyou.service.article;

import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionDetailModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ArticleVersionSearchService {

    List<ArticleVersionDetailModel> searchByArticleId(Long articleId, Boolean hasContent);

    List<ArticleVersionDetailModel> searchByArticleId(Long articleId);

    ArticleVersionDetailModel getDetail(Long id);

    ArticleVersionDetailModel getDetail(Long articleId,Integer version);

    List<ArticleVersionDetailModel> search(Collection<Long> ids,
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
}
