package com.itellyou.service.article;

import com.itellyou.model.article.ArticleVoteModel;

import java.util.Collection;
import java.util.List;

public interface ArticleVoteService {

    List<ArticleVoteModel> search(Collection<Long> articleIds,Long userId);
}
