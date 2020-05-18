package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleInfoDao;
import com.itellyou.model.article.*;
import com.itellyou.model.sys.PageModel;
import com.itellyou.service.article.ArticlePaidReadSearchService;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.article.ArticleVersionSearchService;
import com.itellyou.util.HtmlUtils;
import com.itellyou.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "article")
@Service
public class ArticleSearchServiceImpl implements ArticleSearchService {

    private final ArticleInfoDao articleInfoDao;
    private final ArticlePaidReadSearchService paidReadSearchService;
    private final ArticleVersionSearchService versionSearchService;

    @Autowired
    public ArticleSearchServiceImpl(ArticleInfoDao articleInfoDao, ArticlePaidReadSearchService paidReadSearchService, ArticleVersionSearchService versionSearchService){
        this.articleInfoDao = articleInfoDao;
        this.paidReadSearchService = paidReadSearchService;
        this.versionSearchService = versionSearchService;
    }

    @Override
    public List<ArticleDetailModel> search(HashSet<Long> ids, String mode, Long columnId, Long userId, Long searchUserId, ArticleSourceType sourceType, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
                                           List<Long> tags, Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<ArticleDetailModel> list = articleInfoDao.search(ids,mode,columnId,userId,searchUserId,sourceType,hasContent,isDisabled,isPublished,isDeleted,tags, minComments, maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,ip,order,offset,limit);
        for (ArticleDetailModel detailModel : list){
            ArticlePaidReadModel paidReadModel = paidReadSearchService.findByArticleId(detailModel.getId());
            detailModel.setPaidRead(paidReadModel);
            if(mode != "draft" && paidReadSearchService.checkRead(paidReadModel,detailModel.getCreatedUserId(),searchUserId) == false){
                String content =  HtmlUtils.subEditorContent(detailModel.getContent(),detailModel.getHtml(),paidReadModel.getFreeReadScale());
                detailModel.setContent(content);
                String description;
                if(StringUtils.isEmpty(detailModel.getCustomDescription())){
                    String html = detailModel.getHtml();
                    if(hasContent != null && hasContent == false){
                        ArticleVersionModel versionModel = versionSearchService.find(detailModel.getId(),detailModel.getVersion());
                        if(versionModel != null) html = versionModel.getHtml();
                    }

                    String text = StringUtils.removeHtmlTags(html);
                    int len = new BigDecimal(text.length()).multiply(new BigDecimal(paidReadModel.getFreeReadScale())).intValue();
                    if(len >= text.length()) len = text.length() - 1;
                    if(len <= 0) description = "";
                    else {
                        description = text.substring(0, len);
                        description = StringUtils.getFragmenter(description);
                    }
                    detailModel.setDescription(description);
                }else{
                    detailModel.setDescription(null);
                }
                detailModel.setHtml(null);
            }else{
                detailModel.setPaidRead(null);
            }
        }
        return list;
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
    @Cacheable
    public ArticleInfoModel findById(Long id) {
        return articleInfoDao.findById(id);
    }
}
