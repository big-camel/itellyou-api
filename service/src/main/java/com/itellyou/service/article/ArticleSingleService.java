package com.itellyou.service.article;

import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleTotalModel;
import com.itellyou.model.sys.PageModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ArticleSingleService {
    ArticleInfoModel findById(Long id);
    
    List<ArticleInfoModel> search( Collection<Long> ids,  String mode,  Long columnId,  Long userId,
                                   ArticleSourceType sourceType,
                                   Boolean isDisabled,  Boolean isPublished,  Boolean isDeleted,
                                   Integer minComment,  Integer maxComment,
                                   Integer minView,  Integer maxView,
                                   Integer minSupport,  Integer maxSupport,
                                   Integer minOppose,  Integer maxOppose,
                                   Integer minStar,  Integer maxStar,
                                   Long beginTime,  Long endTime,
                                   Long ip,
                                   Map<String, String> order,
                                   Integer offset,
                                   Integer limit);

    int count(Collection<Long> ids, String mode, Long columnId, Long userId, ArticleSourceType sourceType, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
              Integer minComment, Integer maxComment,
              Integer minView, Integer maxView,
              Integer minSupport, Integer maxSupport,
              Integer minOppose, Integer maxOppose,
              Integer minStars, Integer maxStars,
              Long beginTime, Long endTime, Long ip);

    PageModel<ArticleInfoModel> page(Collection<Long> ids, String mode, Long columnId, Long userId,
                                       ArticleSourceType sourceType,
                                       Boolean isDisabled, Boolean isPublished, Boolean isDeleted,
                                       Integer minComment, Integer maxComment,
                                       Integer minView, Integer maxView,
                                       Integer minSupport, Integer maxSupport,
                                       Integer minOppose, Integer maxOppose,
                                       Integer minStar, Integer maxStar,
                                       Long beginTime, Long endTime,
                                       Long ip,
                                       Map<String, String> order,
                                       Integer offset,
                                       Integer limit);

    List<ArticleTotalModel> totalByUser(Collection<Long> userIds,
                                         Boolean isDisabled,  Boolean isPublished,  Boolean isDeleted,  Long beginTime,  Long endTime,
                                         Map<String, String> order,
                                         Integer offset,
                                         Integer limit);
}
