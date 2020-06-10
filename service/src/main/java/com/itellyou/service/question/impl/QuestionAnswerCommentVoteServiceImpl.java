package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerCommentVoteDao;
import com.itellyou.model.event.AnswerCommentEvent;
import com.itellyou.model.question.QuestionAnswerCommentModel;
import com.itellyou.model.question.QuestionAnswerCommentVoteModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.VoteType;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.service.common.VoteService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.QuestionAnswerCommentSearchService;
import com.itellyou.service.question.QuestionAnswerCommentService;
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

@CacheConfig(cacheNames = "answer_comment_vote")
@Service
public class QuestionAnswerCommentVoteServiceImpl implements VoteService<QuestionAnswerCommentVoteModel> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionAnswerCommentVoteDao voteDao;
    private final QuestionAnswerCommentService commentService;
    private final QuestionAnswerCommentSearchService searchService;
    private final OperationalPublisher operationalPublisher;
    private final VoteSearchService<QuestionAnswerCommentVoteModel> voteSearchService;

    @Autowired
    public QuestionAnswerCommentVoteServiceImpl(QuestionAnswerCommentVoteDao voteDao, QuestionAnswerCommentService commentService, QuestionAnswerCommentSearchService searchService, OperationalPublisher operationalPublisher, QuestionAnswerCommentVoteSearchServiceImpl voteSearchService){
        this.voteDao = voteDao;
        this.commentService = commentService;
        this.searchService = searchService;
        this.operationalPublisher = operationalPublisher;
        this.voteSearchService = voteSearchService;
    }

    @Override
    public int insert(QuestionAnswerCommentVoteModel voteModel) {
        RedisUtils.clear("answer_comment_vote_" + voteModel.getCreatedUserId());
        return voteDao.insert(voteModel);
    }

    @Override
    @CacheEvict(key = "T(String).valueOf(#commentId).concat('-').concat(#userId)")
    public int deleteByTargetIdAndUserId(Long commentId, Long userId) {
        RedisUtils.clear("answer_comment_vote_" + userId);
        return voteDao.deleteByCommentIdAndUserId(commentId,userId);
    }



    @Override
    @Transactional
    @CacheEvict(key = "T(String).valueOf(#id).concat('-').concat(#userId)")
    public Map<String, Object> doVote(VoteType type, Long id, Long userId, Long ip) {
        try{
            QuestionAnswerCommentVoteModel voteModel = voteSearchService.findByTargetIdAndUserId(id,userId);

            if(voteModel != null){
                int result = deleteByTargetIdAndUserId(id,userId);
                if(result != 1) throw new Exception("删除Vote失败");
                result = commentService.updateVote(voteModel.getType(),-1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }
            if(voteModel == null || !voteModel.getType().equals(type)){
                int result = insert(new QuestionAnswerCommentVoteModel(id,type, DateUtils.getTimestamp(),userId, ip));
                if(result != 1) throw new Exception("写入Vote失败");
                result = commentService.updateVote(type,1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }

            QuestionAnswerCommentModel commentModel = searchService.findById(id);
            if(commentModel == null) throw new Exception("获取评论失败");
            if(commentModel.getCreatedUserId().equals((userId))) throw new Exception("不能给自己点赞");
            Map<String,Object> data = new HashMap<>();
            data.put("id",commentModel.getId());
            data.put("parentId",commentModel.getParentId());
            data.put("support",commentModel.getSupport());
            data.put("oppose",commentModel.getOppose());
            if(voteModel != null){
                operationalPublisher.publish(new AnswerCommentEvent(this,
                        voteModel.getType().equals(VoteType.SUPPORT) ? EntityAction.UNLIKE : EntityAction.UNDISLIKE,
                        commentModel.getId(),commentModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
            }
            if(voteModel == null || !voteModel.getType().equals(type)){
                operationalPublisher.publish(new AnswerCommentEvent(this,
                        type.equals(VoteType.SUPPORT) ? EntityAction.LIKE : EntityAction.DISLIKE,
                        commentModel.getId(),commentModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
            }
            RedisUtils.clear("answer_comment_vote_" + userId);
            return data;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }
}
