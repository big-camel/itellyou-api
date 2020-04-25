package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleInfoDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.article.*;
import com.itellyou.service.article.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArticleSearchServiceImpl implements ArticleSearchService {

    private final ArticleInfoDao articleInfoDao;

    @Autowired
    public ArticleSearchServiceImpl(ArticleInfoDao articleInfoDao){
        this.articleInfoDao = articleInfoDao;
    }

    @Override
    public List<ArticleDetailModel> search(HashSet<Long> ids, String mode, Long columnId, Long userId, Long searchUserId, ArticleSourceType sourceType, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
                                           List<Long> tags, Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return articleInfoDao.search(ids,mode,columnId,userId,searchUserId,sourceType,hasContent,isDisabled,isPublished,isDeleted,tags, minComments, maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(HashSet<Long> ids, String mode,Long columnId, Long userId, ArticleSourceType sourceType, Boolean isDisabled,Boolean isDeleted, Boolean isPublished,List<Long> tags,Integer minComments,Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip) {
        return articleInfoDao.count(ids,mode,columnId,userId,sourceType,isDisabled,isPublished,isDeleted,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,ip);
    }

    @Override
    public List<ArticleDetailModel> search(HashSet<Long> ids, String mode,Long columnId, Long userId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(ids,mode,columnId,userId,searchUserId,sourceType,hasContent,null,null,null,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public List<ArticleDetailModel> search(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished,
                                            List<Long> tags,Integer minComments,Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,null,columnId,null,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public List<ArticleDetailModel> search(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished,List<Long> tags,Integer minComments,Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(columnId,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Long columnId, ArticleSourceType sourceType,Boolean isDisabled,Boolean isDeleted, Boolean isPublished,List<Long> tags,Integer minComments,Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime) {
        return count(null,null,columnId,null,sourceType,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,null);
    }

    @Override
    public List<ArticleDetailModel> search(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(columnId,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<ArticleDetailModel> search(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(columnId,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Long columnId, ArticleSourceType sourceType,Boolean isDisabled,Boolean isDeleted, Boolean isPublished, Long beginTime, Long endTime) {
        return count(columnId,sourceType,isDisabled,isDeleted,isPublished,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime);
    }

    @Override
    public PageModel<ArticleDetailModel> page(HashSet<Long> ids, String mode, Long columnId, Long userId, Long searchUserId, ArticleSourceType sourceType, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished, List<Long> tags, Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<ArticleDetailModel> data = search(ids,mode,columnId,userId,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(ids,mode,columnId,userId,sourceType,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public PageModel<ArticleDetailModel> page(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled, Boolean isDeleted, Boolean isPublished, List<Long> tags,Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Map<String, String> order,Integer offset, Integer limit) {
        return page(null,null,columnId,null,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public PageModel<ArticleDetailModel> page(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished, Long beginTime, Long endTime, Integer offset, Integer limit) {
        return page(columnId,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,null,offset,limit);
    }

    @Override
    public ArticleDetailModel getDetail(Long id, String mode, Long userId) {
        List<ArticleDetailModel> listArticle = search(new HashSet<Long>(){{add(id);}},mode,null,userId,null,null,null,null,null,null,0,1);
        return listArticle != null && listArticle.size() > 0 ? listArticle.get(0) : null;
    }

    @Override
    public ArticleDetailModel getDetail(Long id, String mode) {
        return getDetail(id,mode,null);
    }

    @Override
    public ArticleDetailModel getDetail(Long id, Long userId, Long searchUserId) {
        List<ArticleDetailModel> listArticle = search(new HashSet<Long>(){{add(id);}},null,null,userId,searchUserId,null,null,null,null,null,0,1);
        return listArticle != null && listArticle.size() > 0 ? listArticle.get(0) : null;
    }

    @Override
    public ArticleDetailModel getDetail(Long id) {
        return getDetail(id,(String) null);
    }

    @Override
    public ArticleDetailModel getDetail(Long id, Long userId) {
        return getDetail(id,(String) null,userId);
    }

    @Override
    public ArticleInfoModel findById(Long id) {
        return articleInfoDao.findById(id);
    }
}
