package com.itellyou.dao.article;

import com.itellyou.model.article.ArticleVoteModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

@Mapper
@Repository
public interface ArticleVoteDao {
    int insert(ArticleVoteModel voteModel);

    int deleteByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    List<ArticleVoteModel> search(@Param("articleIds") HashSet<Long> articleIds, @Param("userId") Long userId);
}
