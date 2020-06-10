package com.itellyou.service.article;

import com.itellyou.model.article.ArticleCommentVoteModel;

import java.util.HashSet;
import java.util.List;

public interface ArticleCommentVoteService {

    List<ArticleCommentVoteModel> search(HashSet<Long> commentIds, Long userId);
}
