package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleCommentDao;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.article.ArticleCommentModel;
import com.itellyou.model.article.ArticleCommentVoteModel;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.user.UserOperationalAction;
import com.itellyou.model.user.UserOperationalModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.article.*;
import com.itellyou.service.user.UserOperationalService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.Map;

@Service
public class ArticleCommentServiceImpl implements ArticleCommentService {

    private final ArticleCommentDao commentDao;
    private final ArticleInfoService articleService;
    private final ArticleCommentVoteService voteService;
    private final ArticleCommentSearchService commentSearchService;
    private final ArticleSearchService searchService;
    private final ArticleIndexService indexService;
    private final UserOperationalService operationalService;

    @Autowired
    public ArticleCommentServiceImpl(ArticleCommentDao commentDao, ArticleInfoService articleService, ArticleCommentVoteService voteService,ArticleCommentSearchService commentSearchService,ArticleSearchService searchService,ArticleIndexService indexService, UserOperationalService operationalService){
        this.commentDao = commentDao;
        this.articleService = articleService;
        this.voteService = voteService;
        this.commentSearchService = commentSearchService;
        this.searchService = searchService;
        this.indexService = indexService;
        this.operationalService = operationalService;
    }

    @Override
    @Transactional
    public ArticleCommentModel insert(Long articleId, Long parentId, Long replyId, String content , String html, Long userId, String ip) throws Exception {
        try{
            ArticleInfoModel articleModel = searchService.findById(articleId);
            if(articleModel == null) throw new Exception("错误的文章ID");
            ArticleCommentModel replyCommentModel = null;
            if(replyId != null && !replyId.equals(0l)){
                replyCommentModel = commentDao.findById(replyId);
                if(replyCommentModel == null) throw new Exception("错误的回复ID");
            }

            ArticleCommentModel commentModel = new ArticleCommentModel(null,articleId,parentId,replyId,false,content,html,0,0,0, DateUtils.getTimestamp(),userId, IPUtils.toLong(ip),null,null,null);
            int result = commentDao.insert(commentModel);

            if(result != 1) throw new Exception("写入评论失败");
            if(parentId != 0){
                result = updateComments(parentId,1);
                if(result != 1) throw new Exception("更新父级子评论数失败");
            }
            result = articleService.updateComments(articleId,1);
            if(result != 1) throw new Exception("更新文章评论数失败");
            indexService.updateIndex(articleId);

            EntityType notificationType = EntityType.ARTICLE;
            Long targetUserId = articleModel.getCreatedUserId();
            if(replyCommentModel != null){
                notificationType = EntityType.ARTICLE_COMMENT;
                targetUserId = replyCommentModel.getCreatedUserId();
            }
            operationalService.insertAsync(new UserOperationalModel(UserOperationalAction.COMMENT, notificationType,commentModel.getId(),targetUserId,userId, DateUtils.getTimestamp(),IPUtils.toLong(ip)));

            return commentModel;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    public int updateDeleted(Long id, Boolean isDeleted) {
        return commentDao.updateDeleted(id,isDeleted);
    }

    @Override
    public int updateComments(Long id, Integer value) {
        return commentDao.updateComments(id,value);
    }

    @Override
    @Transactional
    public Map<String,Object> updateVote(VoteType type, Long id, Long userId, String ip) {
        try{
            ArticleCommentVoteModel voteModel = voteService.findByCommentIdAndUserId(id,userId);
            if(voteModel != null){
                int result = voteService.deleteByCommentIdAndUserId(id,userId);
                if(result != 1) throw new Exception("删除Vote失败");
                result = commentDao.updateVote(voteModel.getType(),-1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }
            if(voteModel == null || voteModel.getType() != type){
                voteModel = new ArticleCommentVoteModel(id,type,DateUtils.getTimestamp(),userId, IPUtils.toLong(ip));
                int result = voteService.insert(voteModel);
                if(result != 1) throw new Exception("写入Vote失败");
                result = commentDao.updateVote(type,1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }
            ArticleCommentModel commentModel = commentSearchService.findById(id);
            if(commentModel == null) throw new Exception("获取回答失败");
            if(commentModel.getCreatedUserId().equals((userId))) throw new Exception("不能给自己点赞");
            if(type.equals(VoteType.SUPPORT)){
                operationalService.insertAsync(new UserOperationalModel(UserOperationalAction.LIKE, EntityType.ARTICLE_COMMENT,commentModel.getId(),commentModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),IPUtils.toLong(ip)));
            }else{
                operationalService.deleteByTargetIdAsync(UserOperationalAction.LIKE, EntityType.ARTICLE_COMMENT,userId,commentModel.getId());
            }
            Map<String,Object> data = new HashMap<>();
            data.put("id",commentModel.getId());
            data.put("parentId",commentModel.getParentId());
            data.put("support",commentModel.getSupport());
            data.put("oppose",commentModel.getOppose());
            return data;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }
}
