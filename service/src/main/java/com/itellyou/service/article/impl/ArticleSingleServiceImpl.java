package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleInfoDao;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleTotalModel;
import com.itellyou.model.common.DataUpdateStepModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.service.article.ArticleSingleService;
import com.itellyou.service.common.DataUpdateQueueService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = CacheKeys.ARTICLE_KEY)
@Service
public class ArticleSingleServiceImpl implements ArticleSingleService {

    private final ArticleInfoDao infoDao;
    private final DataUpdateQueueService updateQueueService;

    public ArticleSingleServiceImpl(ArticleInfoDao infoDao, DataUpdateQueueService updateQueueService) {
        this.infoDao = infoDao;
        this.updateQueueService = updateQueueService;
    }

    @Override
    public ArticleInfoModel findById(Long id) {
        List<ArticleInfoModel> list = search(new HashSet<Long>(){{ add(id);}},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<ArticleInfoModel> search(Collection<Long> ids, String mode, Long columnId, Long userId, ArticleSourceType sourceType, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Integer minComment, Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset != null && offset < 0) offset = 0;
        if(limit != null && limit < 0) limit = 0;
        Integer finalOffset = offset;
        Integer finalLimit = limit;
        List<ArticleInfoModel> infoModels = RedisUtils.fetch(CacheKeys.ARTICLE_KEY,ArticleInfoModel.class,ids,(Collection<Long> fetchIds) ->
                infoDao.search(fetchIds,mode,columnId,userId,sourceType,isDisabled,isPublished,isDeleted, minComment, maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,ip,order, finalOffset, finalLimit)
        );
        // 从缓存里面计算统计数据值
        List<DataUpdateStepModel> stepModels = updateQueueService.get(EntityType.ARTICLE,infoModels.stream().map(ArticleInfoModel::getId).collect(Collectors.toSet()));
        infoModels.forEach(model -> {
            stepModels.stream().filter(stepModel -> stepModel.getId().equals(model.getId())).findFirst().ifPresent(stepModel -> {
                model.setViewCount(model.getViewCount() + stepModel.getViewStep());
                model.setSupportCount(model.getSupportCount() + stepModel.getSupportStep());
                model.setOpposeCount(model.getOpposeCount() + stepModel.getOpposeStep());
                model.setCommentCount(model.getCommentCount() + stepModel.getCommentStep());
                model.setStarCount(model.getStarCount() + stepModel.getStarStep());
            });
        });
        return infoModels;
    }

    @Override
    public int count(Collection<Long> ids, String mode,Long columnId, Long userId, ArticleSourceType sourceType, Boolean isDisabled,Boolean isDeleted, Boolean isPublished,Integer minComment,Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip) {
        return infoDao.count(ids,mode,columnId,userId,sourceType,isDisabled,isPublished,isDeleted,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,ip);
    }

    @Override
    public PageModel<ArticleInfoModel> page(Collection<Long> ids, String mode, Long columnId, Long userId, ArticleSourceType sourceType, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Integer minComment, Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<ArticleInfoModel> list = search(ids,mode,columnId,userId,sourceType,isDisabled,isPublished,isDeleted, minComment, maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,ip,order,offset,limit);
        Integer count = count(ids,mode,columnId,userId,sourceType,isDisabled,isPublished,isDeleted,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,count,list);
    }

    @Override
    public List<ArticleTotalModel> totalByUser(Collection<Long> userIds, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return infoDao.totalByUser(userIds,isDisabled,isPublished,isDeleted,beginTime,endTime,order,offset,limit);
    }

}
