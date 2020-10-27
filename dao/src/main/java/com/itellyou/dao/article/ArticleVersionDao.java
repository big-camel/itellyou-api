package com.itellyou.dao.article;

import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ArticleVersionDao {
    int insert(ArticleVersionModel versionModel);

    int update(ArticleVersionModel versionModel);

    Integer findVersionById(Long id);

    List<ArticleVersionModel> search(@Param("ids") Collection<Long> ids,
                                     @Param("articleMap") Map<Long,Integer> articleMap,
                                     @Param("userId") Long userId,
                                     @Param("sourceType") ArticleSourceType sourceType,
                                     @Param("hasContent") Boolean hasContent,
                                     @Param("isReviewed") Boolean isReview,
                                     @Param("isDisabled") Boolean isDisable,
                                     @Param("isPublished") Boolean isPublish,
                                     @Param("beginTime") Long beginTime,
                                     @Param("endTime") Long endTime,
                                     @Param("ip") Long ip,
                                     @Param("order") Map<String, String> order,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);

    Integer count (@Param("ids") Collection<Long> ids,
                    @Param("articleMap") Map<Long,Integer> articleMap,
                     @Param("userId") Long userId,
                     @Param("sourceType") ArticleSourceType sourceType,
                     @Param("isReviewed") Boolean isReview,
                     @Param("isDisabled") Boolean isDisable,
                     @Param("isPublished") Boolean isPublish,
                     @Param("beginTime") Long beginTime,
                     @Param("endTime") Long endTime,
                     @Param("ip") Long ip);
}
