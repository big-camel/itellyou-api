package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleVersionDao;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.service.article.ArticleVersionSearchService;
import org.springframework.stereotype.Service;

@Service
public class ArticleVersionSearchServiceImpl implements ArticleVersionSearchService {

    private final ArticleVersionDao versionDao;

    public ArticleVersionSearchServiceImpl(ArticleVersionDao versionDao) {
        this.versionDao = versionDao;
    }

    @Override
    public ArticleVersionModel find(Long articleId, Integer version) {
        return versionDao.findByArticleIdAndVersion(articleId,version);
    }
}
