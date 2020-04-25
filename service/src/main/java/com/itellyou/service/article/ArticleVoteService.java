package com.itellyou.service.article;

import com.itellyou.model.article.ArticleVoteModel;

public interface ArticleVoteService {
    int insert(ArticleVoteModel voteModel);

    int deleteByArticleIdAndUserId(Long articleId, Long userId);

    ArticleVoteModel findByArticleIdAndUserId(Long articleId, Long userId);
}
