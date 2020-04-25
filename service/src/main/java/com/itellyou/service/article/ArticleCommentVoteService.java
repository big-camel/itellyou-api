package com.itellyou.service.article;

import com.itellyou.model.article.ArticleCommentVoteModel;

public interface ArticleCommentVoteService {
    int insert(ArticleCommentVoteModel voteModel);

    int deleteByCommentIdAndUserId(Long commentId, Long userId);

    ArticleCommentVoteModel findByCommentIdAndUserId(Long commentId, Long userId);
}
