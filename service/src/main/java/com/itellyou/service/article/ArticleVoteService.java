package com.itellyou.service.article;

import com.itellyou.model.article.ArticleVoteModel;

import java.util.HashSet;
import java.util.List;

public interface ArticleVoteService {

    List<ArticleVoteModel> search(HashSet<Long> articleIds,Long userId);
}
