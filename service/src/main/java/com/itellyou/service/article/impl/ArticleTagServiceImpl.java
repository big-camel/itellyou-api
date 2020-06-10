package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleTagDao;
import com.itellyou.model.article.ArticleTagModel;
import com.itellyou.service.article.ArticleTagService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@CacheConfig(cacheNames = "article_tag")
public class ArticleTagServiceImpl implements ArticleTagService {

    private final ArticleTagDao articleTagDao;

    public ArticleTagServiceImpl(ArticleTagDao articleTagDao) {
        this.articleTagDao = articleTagDao;
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#model.articleId") , @CacheEvict(value = "tag_article" , allEntries = true)})
    public int add(ArticleTagModel model) {
        return articleTagDao.add(model);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#articleId") , @CacheEvict(value = "tag_article" , allEntries = true)})
    public int addAll(Long articleId, HashSet<Long> tagIds) {
        return articleTagDao.addAll(articleId,tagIds);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#articleId") , @CacheEvict(value = "tag_article" , allEntries = true)})
    public int clear(Long articleId) {
        return articleTagDao.clear(articleId);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#articleId") , @CacheEvict(value = "tag_article" , allEntries = true)})
    public int remove(Long articleId, Long tagId) {
        return articleTagDao.remove(articleId,tagId);
    }

    @Override
    public Map<Long, List<ArticleTagModel>> searchTags(HashSet<Long> articleIds) {
        List<ArticleTagModel> models = articleTagDao.searchTags(articleIds);
        Map<Long, List<ArticleTagModel>> map = new LinkedHashMap<>();
        for (ArticleTagModel model : models){
            if(!map.containsKey(model.getArticleId())){
                map.put(model.getArticleId(),new LinkedList<>());
            }
            map.get(model.getArticleId()).add(model);
        }
        return map;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public HashSet<Long> searchTagId(Long articleId) {
        return articleTagDao.searchTagId(articleId);
    }

    @Override
    @Cacheable(value = "tag_article",key = "#tagId",unless = "#result == null")
    public HashSet<Long> searchArticleId(Long tagId) {
        return searchArticleId(tagId != null ? new HashSet<Long>(){{ add(tagId);}} : null);
    }

    @Override
    @Cacheable(value = "tag_article",key = "#methodName",unless = "#result == null")
    public HashSet<Long> searchArticleId(HashSet<Long> tagId) {
        return articleTagDao.searchArticleId(tagId);
    }
}
