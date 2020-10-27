package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleStarDao;
import com.itellyou.model.article.ArticleStarModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.service.article.ArticleStarSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = CacheKeys.ARTICLE_STAR_KEY)
@Service
public class ArticleStarSingleServiceImpl implements ArticleStarSingleService {

    private final ArticleStarDao starDao;

    public ArticleStarSingleServiceImpl(ArticleStarDao starDao) {
        this.starDao = starDao;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#articleId).concat('-').concat(#userId)",unless = "#result == null")
    public ArticleStarModel find(Long articleId, Long userId) {
        List<ArticleStarModel> starModels = starDao.search(articleId !=null ? new HashSet<Long>(){{add(articleId);}} : null,userId,null,null,null,null,null,null);
        return starModels != null && starModels.size() > 0 ? starModels.get(0) : null;
    }

    @Override
    public List<ArticleStarModel> search(Collection<Long> articleIds, Long userId) {
        return RedisUtils.fetch(CacheKeys.ARTICLE_STAR_KEY, ArticleStarModel.class,articleIds,(Collection<Long> fetchIds) ->
                starDao.search(fetchIds,userId,null,null,null,null,null,null),
                id -> id + "-" + userId,
                ArticleStarModel::cacheKey);
    }
}
