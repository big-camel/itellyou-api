package com.itellyou.dao.article;

import com.itellyou.model.article.ArticlePaidReadModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ArticlePaidReadDao {

    int insert(ArticlePaidReadModel model);

    int deleteByArticleId(Long articleId);

    ArticlePaidReadModel findByArticleId(Long articleId);
}
