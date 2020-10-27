package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleTagDao;
import com.itellyou.model.article.ArticleTagModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.service.article.ArticleTagService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = CacheKeys.ARTICLE_TAG_KEY)
public class ArticleTagServiceImpl implements ArticleTagService {

    private final ArticleTagDao articleTagDao;

    public ArticleTagServiceImpl(ArticleTagDao articleTagDao) {
        this.articleTagDao = articleTagDao;
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#model.articleId") , @CacheEvict(value = CacheKeys.TAG_ARTICLE_KEY, key = "#model.tagId")})
    public int add(ArticleTagModel model) {
        return articleTagDao.add(model);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#articleId") })
    public int addAll(Long articleId, Collection<Long> tagIds) {
        tagIds.forEach((Long id) -> RedisUtils.remove(CacheKeys.TAG_ARTICLE_KEY,id));
        return articleTagDao.addAll(articleId,tagIds);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#articleId") })
    public int clear(Long articleId) {
        Map<Long, List<ArticleTagModel>> tags = searchTags(new HashSet<Long>(){{add(articleId);}});
        tags.values().forEach((List<ArticleTagModel> models) ->
                models.forEach((ArticleTagModel model) -> RedisUtils.remove(CacheKeys.TAG_ARTICLE_KEY,model.getTagId()))
        );
        return articleTagDao.clear(articleId);
    }

    @Override
    @Caching( evict = { @CacheEvict(key = "#articleId") , @CacheEvict(value = CacheKeys.TAG_ARTICLE_KEY , key = "#tagId" )})
    public int remove(Long articleId, Long tagId) {
        return articleTagDao.remove(articleId,tagId);
    }

    @Override
    public Map<Long, List<ArticleTagModel>> searchTags(Collection<Long> articleIds) {
        return RedisUtils.fetch(CacheKeys.ARTICLE_TAG_KEY,articleIds,
                (Collection<Long> fetchIds) -> articleTagDao.searchTags((Collection<Long>) fetchIds),
                ArticleTagModel::getArticleId);
    }

    @Override
    public Map<Long, List<ArticleTagModel>> searchArticles(Collection<Long> tagIds) {
        return RedisUtils.fetch(CacheKeys.TAG_ARTICLE_KEY,tagIds,
                (Collection<Long> fetchIds) -> articleTagDao.searchArticles((Collection<Long>)fetchIds),
                ArticleTagModel::getTagId);
    }

    @Override
    public Collection<Long> searchArticleIds(Collection<Long> tagIds) {
        Map<Long, List<ArticleTagModel>> list = searchArticles(tagIds);
        Collection<Long> ids = new LinkedHashSet<>();
        for (Long id : list.keySet()){
            ids.addAll(list.get(id).stream().map(ArticleTagModel::getArticleId).collect(Collectors.toSet()));
        }
        return ids;
    }
}
