package com.itellyou.service.article;

import com.itellyou.model.article.ArticleCommentVoteModel;

import java.util.Collection;
import java.util.List;

public interface ArticleCommentVoteService {

    List<ArticleCommentVoteModel> search(Collection<Long> commentIds, Long userId);
}
