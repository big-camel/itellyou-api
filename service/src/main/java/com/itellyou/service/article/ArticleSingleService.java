package com.itellyou.service.article;

import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleSourceType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface ArticleSingleService {
    ArticleInfoModel findById(Long id);
    
    List<ArticleInfoModel> search( HashSet<Long> ids,  String mode,  Long columnId,  Long userId,
                                   ArticleSourceType sourceType,
                                   Boolean isDisabled,  Boolean isPublished,  Boolean isDeleted,
                                   Integer minComments,  Integer maxComments,
                                   Integer minView,  Integer maxView,
                                   Integer minSupport,  Integer maxSupport,
                                   Integer minOppose,  Integer maxOppose,
                                   Integer minStar,  Integer maxStar,
                                   Long beginTime,  Long endTime,
                                   Long ip,
                                   Map<String, String> order,
                                   Integer offset,
                                   Integer limit);
}
