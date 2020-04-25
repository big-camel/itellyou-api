package com.itellyou.dao.article;

import com.itellyou.model.article.ArticleCommentVoteModel;
import com.itellyou.model.article.ArticleVoteModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ArticleVoteDao {
    int insert(ArticleVoteModel voteModel);

    int deleteByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    ArticleVoteModel findByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);
}
