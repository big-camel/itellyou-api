package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleVoteDao;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleVoteModel;
import com.itellyou.model.event.ArticleEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.VoteType;
import com.itellyou.service.article.ArticleInfoService;
import com.itellyou.service.article.ArticleSingleService;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.service.common.VoteService;
import com.itellyou.service.event.OperationalPublisher;
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

import java.util.HashMap;
import java.util.Map;

@CacheConfig(cacheNames = "article_vote")
@Service
public class ArticleVoteServiceImpl implements VoteService<ArticleVoteModel> {

    private Logger logger = LoggerFactory.getLogger(ArticleVoteServiceImpl.class);

    private final ArticleVoteDao voteDao;
    private final ArticleInfoService infoService;
    private final ArticleSingleService searchService;
    private final VoteSearchService<ArticleVoteModel> voteSearchService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public ArticleVoteServiceImpl(ArticleVoteDao voteDao, ArticleInfoService infoService, ArticleSingleService searchService, ArticleVoteSearchServiceImpl voteSearchService, OperationalPublisher operationalPublisher){
        this.voteDao = voteDao;
        this.infoService = infoService;
        this.searchService = searchService;
        this.voteSearchService = voteSearchService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    public int insert(ArticleVoteModel voteModel) {
        RedisUtils.clear("article_vote_" + voteModel.getCreatedUserId());
        return voteDao.insert(voteModel);
    }

    @Override
    @CacheEvict(key = "T(String).valueOf(#articleId).concat('-').concat(#userId)")
    public int deleteByTargetIdAndUserId(Long articleId, Long userId) {
        RedisUtils.clear("article_vote_" + userId);
        return voteDao.deleteByArticleIdAndUserId(articleId,userId);
    }

    @Override
    @CacheEvict(key = "T(String).valueOf(#id).concat('-').concat(#userId)")
    @Transactional
    public Map<String, Object> doVote(VoteType type, Long id, Long userId, Long ip) {
        try{
            Map<String,Object> data = new HashMap<>();
            ArticleVoteModel voteModel = voteSearchService.findByTargetIdAndUserId(id,userId);

            if(voteModel != null){
                int result = deleteByTargetIdAndUserId(id,userId);
                if(result != 1) throw new Exception("删除Vote失败");
                result = infoService.updateVote(voteModel.getType(),-1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }
            if(voteModel == null || !voteModel.getType().equals(type)){
                int result = insert(new ArticleVoteModel(id,type, DateUtils.getTimestamp(),userId, ip));
                if(result != 1) throw new Exception("写入Vote失败");
                result = infoService.updateVote(type,1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }

            ArticleInfoModel infoModel = searchService.findById(id);
            if(infoModel == null) throw new Exception("获取文章失败");
            if(infoModel.getCreatedUserId().equals(userId)) throw new Exception("不能自己给自己点赞");
            data.put("id",infoModel.getId());
            data.put("support",infoModel.getSupport());
            data.put("oppose",infoModel.getOppose());
            if(voteModel != null){
                operationalPublisher.publish(new ArticleEvent(this,
                        voteModel.getType().equals(VoteType.SUPPORT) ? EntityAction.UNLIKE : EntityAction.UNDISLIKE,
                        infoModel.getId(),infoModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
            }
            if(voteModel == null || !voteModel.getType().equals(type)){
                operationalPublisher.publish(new ArticleEvent(this,
                        type.equals(VoteType.SUPPORT) ? EntityAction.LIKE : EntityAction.DISLIKE,
                        infoModel.getId(),infoModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
            }
            RedisUtils.clear("article_vote_" + userId);
            return data;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }
}
