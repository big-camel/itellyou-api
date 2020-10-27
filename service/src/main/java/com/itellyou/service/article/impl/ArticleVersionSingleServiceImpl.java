package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleVersionDao;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.service.article.ArticleVersionSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = CacheKeys.ARTICLE_VERSION_KEY)
@Service
public class ArticleVersionSingleServiceImpl implements ArticleVersionSingleService {

    private final ArticleVersionDao versionDao;

    public ArticleVersionSingleServiceImpl(ArticleVersionDao versionDao) {
        this.versionDao = versionDao;
    }

    @Override
    public ArticleVersionModel find(Long articleId, Integer version) {
        List<ArticleVersionModel> list = search(null, new HashMap<Long, Integer>(){{put(articleId,version);}}, null, null, true, null, null, null, null, null, null, null, null, null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }

    @Override
    public List<ArticleVersionModel> search(Collection<Long> ids, Map<Long, Integer> articleMap, Long userId, ArticleSourceType sourceType, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<ArticleVersionModel> versionModels = RedisUtils.fetch(CacheKeys.ARTICLE_VERSION_KEY, ArticleVersionModel.class,ids,(Collection<Long> fetchIds) ->
                versionDao.search(fetchIds,articleMap,userId,sourceType,hasContent,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit),
                id -> id + (hasContent == null || hasContent == true ? "" : "-nc"),
                // 如果没有传ids，那么使用默认cacheKey缓存数据
                model -> (ids != null && ids.size() > 0 ? model.getId() : model.cacheKey()) + (hasContent == null || hasContent == true ? "" : "-nc")
        );
        return  versionModels;
    }

    @Override
    public Integer count(Collection<Long> ids, Map<Long, Integer> articleMap, Long userId, ArticleSourceType sourceType, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip) {
        return versionDao.count(ids,articleMap,userId,sourceType,isReview,isDisable,isPublish,beginTime,endTime,ip);
    }

    @Override
    public Integer findVersionById(Long id) {
        return versionDao.findVersionById(id);
    }

    @Override
    public List<ArticleVersionModel> searchByArticleMap(Map<Long, Integer> articleMap, Boolean hasContent) {
        return RedisUtils.fetch(CacheKeys.ARTICLE_VERSION_KEY, ArticleVersionModel.class,articleMap,(Map<Long, Integer> fetchMap) ->
                        search(null,fetchMap,null,null,hasContent,null,null,null,null,null,null,null,null,null),
                (key,value) -> key + "-" + value + (hasContent == null || hasContent == true ? "" : "-nc"),
                model -> model.cacheKey() + (hasContent == null || hasContent == true ? "" : "-nc"));
    }

    @Override
    public ArticleVersionModel find(Long id) {
        List<ArticleVersionModel> list = search(id != null ? new HashSet<Long>(){{add(id);}} : null, null, null, null, true, null, null, null, null, null, null, null, null, null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }
}
