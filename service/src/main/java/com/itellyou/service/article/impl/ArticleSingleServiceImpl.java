package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleInfoDao;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.service.article.ArticleSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "article")
@Service
public class ArticleSingleServiceImpl implements ArticleSingleService {

    private final ArticleInfoDao infoDao;

    public ArticleSingleServiceImpl(ArticleInfoDao infoDao) {
        this.infoDao = infoDao;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public ArticleInfoModel findById(Long id) {
        return infoDao.findById(id);
    }

    @Override
    public List<ArticleInfoModel> search(HashSet<Long> ids, String mode, Long columnId, Long userId, ArticleSourceType sourceType, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetchByCache("article",ArticleInfoModel.class,ids,(HashSet<Long> fetchIds) ->
                infoDao.search(fetchIds,mode,columnId,userId,sourceType,isDisabled,isPublished,isDeleted, minComments, maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,ip,order,offset,limit)
        );
    }
}
