package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleInfoDao;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.event.ArticleEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.VoteType;
import com.itellyou.service.article.ArticleInfoService;
import com.itellyou.service.article.ArticleSingleService;
import com.itellyou.service.column.ColumnInfoService;
import com.itellyou.service.common.ViewService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = "article")
@Service
public class ArticleInfoServiceImpl implements ArticleInfoService {

    private Logger logger = LoggerFactory.getLogger(ArticleInfoServiceImpl.class);

    private final ArticleInfoDao articleInfoDao;
    private final ArticleSingleService articleSingleService;
    private final ViewService viewService;
    private final UserInfoService userInfoService;
    private final UserDraftService draftService;
    private final ColumnInfoService columnInfoService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public ArticleInfoServiceImpl(ArticleInfoDao articleInfoDao, ArticleSingleService articleSingleService, ViewService viewService, UserInfoService userInfoService, UserDraftService draftService, ColumnInfoService columnInfoService, OperationalPublisher operationalPublisher){
        this.articleInfoDao = articleInfoDao;
        this.articleSingleService = articleSingleService;
        this.viewService = viewService;
        this.userInfoService = userInfoService;
        this.draftService = draftService;
        this.columnInfoService = columnInfoService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    public int insert(ArticleInfoModel articleInfoModel) {
        return articleInfoDao.insert(articleInfoModel);
    }

    @Override
    @Transactional
    public int updateView(Long userId,Long id,Long ip,String os,String browser) {
        try{
            ArticleInfoModel articleModel = articleSingleService.findById(id);
            if(articleModel == null) throw new Exception("错误的编号");

            long prevTime = viewService.insertOrUpdate(userId,EntityType.ARTICLE,id,ip,os,browser);
            if(DateUtils.getTimestamp() - prevTime > 3600){
                int result = articleInfoDao.updateView(id,1);
                if(result != 1){
                    throw new Exception("更新浏览次数失败");
                }
                articleModel.setView(articleModel.getView() + 1);
                RedisUtils.setCache("article",id,articleModel);
                operationalPublisher.publish(new ArticleEvent(this, EntityAction.VIEW,id,articleModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
            }
            return 1;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateComments(Long id, Integer value) {
        return articleInfoDao.updateComments(id,value);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateStars(Long id, Integer value) {
        return articleInfoDao.updateStars(id,value);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateMetas(Long id, String customDescription, String cover) {
        return articleInfoDao.updateMetas(id,customDescription,cover);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateVote(VoteType type, Integer value, Long id) {
        return articleInfoDao.updateVote(type,value,id);
    }


    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public int updateDeleted(boolean deleted, Long id,Long userId,Long ip) {
        try {
            ArticleInfoModel articleInfoModel = articleSingleService.findById(id);
            if(articleInfoModel == null) throw new Exception("未找到文章");
            if(!articleInfoModel.getCreatedUserId().equals(userId)) throw new Exception("无权限");
            int result = articleInfoDao.updateDeleted(deleted,id);
            if(result != 1)throw new Exception("删除失败");
            if(deleted){
                //删除用户草稿
                draftService.delete(userId, EntityType.ARTICLE,id);
            }
            result = userInfoService.updateArticleCount(userId,deleted ? -1 : 1);
            if(result != 1) throw new Exception("更新用户文章数量失败");
            Long columnId = articleInfoModel.getColumnId();
            if(columnId != null){
                result = columnInfoService.updateArticles(columnId,deleted ? -1 : 1);
                if(result != 1) throw new Exception("更新专栏文章数量失败");
            }
            operationalPublisher.publish(new ArticleEvent(this,deleted ? EntityAction.DELETE : EntityAction.REVERT,
                    id,articleInfoModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
            return result;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateInfo(Long id, String title, String description, Long columnId, ArticleSourceType sourceType, String sourceData,Long time,
                          Long ip,
                          Long userId) {
        return articleInfoDao.updateInfo(id,title,description,columnId,sourceType,sourceData,time,ip,userId);
    }
}
