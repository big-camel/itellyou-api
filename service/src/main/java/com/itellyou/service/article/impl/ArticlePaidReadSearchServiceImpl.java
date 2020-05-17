package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticlePaidReadDao;
import com.itellyou.model.article.ArticlePaidReadModel;
import com.itellyou.service.article.ArticlePaidReadSearchService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "article_paid_read")
@Service
public class ArticlePaidReadSearchServiceImpl implements ArticlePaidReadSearchService {
    private final ArticlePaidReadDao articlePaidReadDao;

    public ArticlePaidReadSearchServiceImpl(ArticlePaidReadDao articlePaidReadDao) {
        this.articlePaidReadDao = articlePaidReadDao;
    }

    @Override
    @Cacheable(key = "#articleId")
    public ArticlePaidReadModel findByArticleId(Long articleId) {
        return articlePaidReadDao.findByArticleId(articleId);
    }
}
