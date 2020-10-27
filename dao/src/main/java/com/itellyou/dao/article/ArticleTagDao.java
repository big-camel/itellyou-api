package com.itellyou.dao.article;

import com.itellyou.model.article.ArticleTagModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface ArticleTagDao {
    int add(ArticleTagModel model);

    int addAll(@Param("articleId") Long articleId,@Param("tagIds") Collection<Long> tagIds);

    int clear(Long articleId);

    int remove(@Param("articleId") Long articleId,@Param("tagId") Long tagId);

    List<ArticleTagModel> searchTags(@Param("articleIds")Collection<Long> articleIds);

    List<ArticleTagModel> searchArticles(@Param("tagIds") Collection<Long> tagIds);
}
