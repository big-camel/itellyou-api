package com.itellyou.service.article;

import com.itellyou.model.article.ArticleVersionModel;

public interface ArticleVersionService {
    int insert(ArticleVersionModel versionModel);

    int update(ArticleVersionModel versionModel);
}
