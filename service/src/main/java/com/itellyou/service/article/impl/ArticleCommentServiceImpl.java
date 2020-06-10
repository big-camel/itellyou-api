package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleCommentDao;
import com.itellyou.model.article.ArticleCommentModel;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.event.ArticleCommentEvent;
import com.itellyou.model.event.ArticleEvent;
import com.itellyou.model.event.OperationalEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.VoteType;
import com.itellyou.service.article.ArticleCommentService;
import com.itellyou.service.article.ArticleInfoService;
import com.itellyou.service.article.ArticleSingleService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = "article_comment")
@Service
public class ArticleCommentServiceImpl implements ArticleCommentService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArticleCommentDao commentDao;
    private final ArticleInfoService articleService;
    private final ArticleSingleService searchService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public ArticleCommentServiceImpl(ArticleCommentDao commentDao, ArticleInfoService articleService, ArticleSingleService searchService, OperationalPublisher operationalPublisher){
        this.commentDao = commentDao;
        this.articleService = articleService;
        this.searchService = searchService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#parentId")
    public ArticleCommentModel insert(Long articleId, Long parentId, Long replyId, String content, String html, Long userId, Long ip,Boolean sendEvent) throws Exception {
        try{
            ArticleInfoModel articleModel = searchService.findById(articleId);
            if(articleModel == null) throw new Exception("错误的文章ID");
            ArticleCommentModel replyCommentModel = null;
            if(replyId != null && !replyId.equals(0l)){
                replyCommentModel = commentDao.findById(replyId);
                if(replyCommentModel == null) throw new Exception("错误的回复ID");
            }

            ArticleCommentModel commentModel = new ArticleCommentModel(null,articleId,parentId,replyId,false,content,html,0,0,0, DateUtils.getTimestamp(),userId, ip,null,null,null);
            int result = commentDao.insert(commentModel);

            if(result != 1) throw new Exception("写入评论失败");
            if(parentId != 0){
                result = updateComments(parentId,1);
                if(result != 1) throw new Exception("更新父级子评论数失败");
            }
            result = articleService.updateComments(articleId,1);
            if(result != 1) throw new Exception("更新文章评论数失败");

            EntityType notificationType = EntityType.ARTICLE;
            Long targetUserId = articleModel.getCreatedUserId();
            if(replyCommentModel != null){
                notificationType = EntityType.ARTICLE_COMMENT;
                targetUserId = replyCommentModel.getCreatedUserId();
            }
            if(sendEvent == true) {
                OperationalEvent event = notificationType.equals(EntityType.ARTICLE) ?

                        new ArticleEvent(this, EntityAction.COMMENT,
                                commentModel.getId(), targetUserId, userId, DateUtils.getTimestamp(), ip) :

                        new ArticleCommentEvent(this, EntityAction.COMMENT, commentModel.getId(), targetUserId, userId, DateUtils.getTimestamp(), ip);
                operationalPublisher.publish(event);
            }
            return commentModel;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateDeleted(Long id, Boolean isDeleted,Long userId,Long ip) {
        ArticleCommentModel commentModel = commentDao.findById(id);
        if(commentModel == null) return 0;
        if(!commentModel.getCreatedUserId().equals(userId)) return 0;
        int result = commentDao.updateDeleted(id,isDeleted);
        if(result != 1) return 0;
        operationalPublisher.publish(new ArticleCommentEvent(this, isDeleted ? EntityAction.DELETE : EntityAction.REVERT,
                commentModel.getId(),commentModel.getCreatedUserId(),userId,DateUtils.getTimestamp(),ip));
        return 1;
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateComments(Long id, Integer value) {
        return commentDao.updateComments(id,value);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateVote(VoteType type, Integer value, Long id) {
        return commentDao.updateVote(type,value,id);
    }
}
