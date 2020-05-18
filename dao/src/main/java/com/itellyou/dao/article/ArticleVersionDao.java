package com.itellyou.dao.article;

import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.model.tag.TagInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ArticleVersionDao {
    int insert(ArticleVersionModel versionModel);

    int update(ArticleVersionModel versionModel);

    int insertTag(@Param("version") Long version, @Param("tags") TagInfoModel... tags);

    int deleteTag(Long version);

    Integer findVersionById(Long id);

    ArticleVersionModel findByArticleIdAndVersion(@Param("articleId") Long articleId,@Param("version") Integer version);

    List<ArticleVersionModel> search(@Param("id") Long id,
                                     @Param("articleId") Long articleId,
                                     @Param("userId") String userId,
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

    Integer count (@Param("id") Long id,
                     @Param("articleId") Long articleId,
                     @Param("userId") String userId,
                     @Param("sourceType") ArticleSourceType sourceType,
                     @Param("isReviewed") Boolean isReview,
                     @Param("isDisabled") Boolean isDisable,
                     @Param("isPublished") Boolean isPublish,
                     @Param("beginTime") Long beginTime,
                     @Param("endTime") Long endTime,
                     @Param("ip") Long ip);
}
