package com.itellyou.dao.article;

import com.itellyou.model.article.ArticlePaidReadModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface ArticlePaidReadDao {

    int insert(ArticlePaidReadModel model);

    int deleteByArticleId(Long articleId);

    ArticlePaidReadModel findByArticleId(Long articleId);

    List<ArticlePaidReadModel> search(@Param("articleIds") Collection<Long> articleIds);
}
