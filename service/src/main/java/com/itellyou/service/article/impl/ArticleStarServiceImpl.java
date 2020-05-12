package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleStarDao;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleStarDetailModel;
import com.itellyou.model.article.ArticleStarModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.event.ArticleEvent;
import com.itellyou.model.sys.PageModel;
import com.itellyou.service.article.ArticleInfoService;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.common.StarService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "article")
@Service
public class ArticleStarServiceImpl implements StarService<ArticleStarModel> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArticleStarDao starDao;
    private final ArticleInfoService infoService;
    private final ArticleSearchService searchService;
    private final UserInfoService userService;
    private final OperationalPublisher operationalPublisher;

    public ArticleStarServiceImpl(ArticleStarDao starDao, ArticleInfoService infoService, ArticleSearchService searchService, UserInfoService userService, OperationalPublisher operationalPublisher){
        this.starDao = starDao;
        this.infoService = infoService;
        this.searchService = searchService;
        this.userService = userService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#model.articleId")
    public int insert(ArticleStarModel model) throws Exception {
        ArticleInfoModel infoModel = searchService.findById(model.getArticleId());
        try{
            if(infoModel == null) throw new Exception("错误的文章ID");
            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入收藏记录失败");
            result = infoService.updateStars(model.getArticleId(),1);
            if(result != 1) throw new Exception("更新收藏数失败");
            result = userService.updateCollectionCount(model.getCreatedUserId(),1);
            if(result != 1) throw new Exception("更新用户收藏数失败");
            operationalPublisher.publish(new ArticleEvent(this, EntityAction.FOLLOW,model.getArticleId(),infoModel.getCreatedUserId(),model.getCreatedUserId(), DateUtils.getTimestamp(),model.getCreatedIp()));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        return 1;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#articleId")
    public int delete(Long articleId, Long userId,Long ip) throws Exception {
        ArticleInfoModel infoModel = searchService.findById(articleId);
        try{
            if(infoModel == null) throw new Exception("错误的文章ID");
            int result = starDao.delete(articleId,userId);
            if(result != 1) throw new Exception("删除收藏记录失败");
            result = infoService.updateStars(articleId,-1);
            if(result != 1) throw new Exception("更新收藏数失败");
            result = userService.updateCollectionCount(userId,-1);
            if(result != 1) throw new Exception("更新用户收藏数失败");
            operationalPublisher.publish(new ArticleEvent(this, EntityAction.UNFOLLOW,articleId,infoModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));

        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        return 1;
    }

    @Override
    public List<ArticleStarDetailModel> search(Long articleId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return starDao.search(articleId,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(Long articleId, Long userId, Long beginTime, Long endTime, Long ip) {
        return starDao.count(articleId,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<ArticleStarDetailModel> page(Long articleId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<ArticleStarDetailModel> data = search(articleId,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(articleId,userId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
