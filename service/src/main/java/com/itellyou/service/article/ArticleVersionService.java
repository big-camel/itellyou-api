package com.itellyou.service.article;

import com.itellyou.model.article.ArticleVersionModel;

public interface ArticleVersionService {
    int insert(ArticleVersionModel versionModel);

    int update(ArticleVersionModel versionModel);

    int updateVersion(Long articleId, Integer version, Long ip, Long user);

    int updateVersion(Long articleId, Integer version, Boolean isPublished, Long ip, Long user);

    int updateVersion(Long articleId, Integer version, Integer draft, Boolean isPublished, Long time, Long ip, Long user);

    int updateVersion(ArticleVersionModel versionModel);

    int updateDraft(Long articleId, Integer version, Boolean isPublished, Long time, Long ip, Long user);

    int updateDraft(Long articleId, Integer version, Long time, Long ip, Long user);

    int updateDraft(ArticleVersionModel versionModel);
}
