package com.itellyou.dao.article;

import com.itellyou.model.article.ArticleCommentVoteModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ArticleCommentVoteDao {
    int insert(ArticleCommentVoteModel voteModel);

    int deleteByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    ArticleCommentVoteModel findByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);
}
