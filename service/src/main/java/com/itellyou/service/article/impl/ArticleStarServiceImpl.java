package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleStarDao;
import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleStarDetailModel;
import com.itellyou.model.article.ArticleStarModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.event.ArticleEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.article.ArticleSingleService;
import com.itellyou.service.common.StarService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@CacheConfig(cacheNames = CacheKeys.ARTICLE_STAR_KEY)
@Service
public class ArticleStarServiceImpl implements StarService<ArticleStarModel> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArticleStarDao starDao;
    private final ArticleSingleService singleService;
    private final ArticleSearchService searchService;
    private final UserInfoService userService;
    private final UserSearchService userSearchService;
    private final OperationalPublisher operationalPublisher;

    public ArticleStarServiceImpl(ArticleStarDao starDao, ArticleSingleService singleService, ArticleSearchService searchService, UserInfoService userService, UserSearchService userSearchService, OperationalPublisher operationalPublisher){
        this.starDao = starDao;
        this.singleService = singleService;
        this.searchService = searchService;
        this.userService = userService;
        this.userSearchService = userSearchService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    @CacheEvict(key = "T(String).valueOf(#model.articleId).concat('-').concat(#model.createdUserId)")
    public int insert(ArticleStarModel model) throws Exception {
        ArticleInfoModel infoModel = singleService.findById(model.getArticleId());
        try{
            if(infoModel == null) throw new Exception("错误的文章ID");
            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入收藏记录失败");
            result = userService.updateCollectionCount(model.getCreatedUserId(),1);
            if(result != 1) throw new Exception("更新用户收藏数失败");
            operationalPublisher.publish(new ArticleEvent(this, EntityAction.FOLLOW,model.getArticleId(),infoModel.getCreatedUserId(),model.getCreatedUserId(), DateUtils.toLocalDateTime(),model.getCreatedIp()));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        return infoModel.getStarCount() + 1;
    }

    @Override
    @Transactional
    @CacheEvict(key = "T(String).valueOf(#articleId).concat('-').concat(#userId)")
    public int delete(Long articleId, Long userId,Long ip) throws Exception {
        ArticleInfoModel infoModel = singleService.findById(articleId);
        try{
            if(infoModel == null) throw new Exception("错误的文章ID");
            int result = starDao.delete(articleId,userId);
            if(result != 1) throw new Exception("删除收藏记录失败");
            result = userService.updateCollectionCount(userId,-1);
            if(result != 1) throw new Exception("更新用户收藏数失败");
            operationalPublisher.publish(new ArticleEvent(this, EntityAction.UNFOLLOW,articleId,infoModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));

        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        return infoModel.getStarCount() - 1;
    }

    @Override
    public List<ArticleStarDetailModel> search(Collection<Long> articleId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<ArticleStarModel> models = starDao.search(articleId,userId,beginTime,endTime,ip,order,offset,limit);
        List<ArticleStarDetailModel> detailModels = new ArrayList<>();
        if(models.size() == 0) return detailModels;
        Collection<Long> articleIds = new LinkedHashSet<>();
        Collection<Long> userIds = new LinkedHashSet<>();

        for (ArticleStarModel model : models){
            ArticleStarDetailModel detailModel = new ArticleStarDetailModel();
            detailModel.setArticleId(model.getArticleId());
            detailModel.setCreatedIp(model.getCreatedIp());
            detailModel.setCreatedTime(model.getCreatedTime());
            detailModel.setCreatedUserId(model.getCreatedUserId());

            articleIds.add(model.getArticleId());
            userIds.add(model.getCreatedUserId());

            detailModels.add(detailModel);
        }

        List<ArticleDetailModel> articleDetailModels = searchService.search(articleIds,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        List<UserDetailModel> userDetailModels = userSearchService.search(userIds,null,null,null,null,null,null,null,null,null,null,null);
        for (ArticleStarDetailModel detailModel : detailModels){
            for (ArticleDetailModel articleDetailModel :  articleDetailModels){
                if(articleDetailModel.getId().equals(detailModel.getArticleId())){
                    detailModel.setArticle(articleDetailModel);
                    break;
                }
            }
            // 设置对应的作者
            for (UserDetailModel userDetailModel : userDetailModels){
                if(detailModel.getCreatedUserId().equals(userDetailModel.getCreatedUserId())){
                    detailModel.setUser(userDetailModel);
                    break;
                }
            }
        }
        return detailModels;
    }

    @Override
    public int count(Collection<Long> articleId, Long userId, Long beginTime, Long endTime, Long ip) {
        return starDao.count(articleId,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<ArticleStarDetailModel> page(Collection<Long> articleId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<ArticleStarDetailModel> data = search(articleId,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(articleId,userId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
