package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerVoteDao;
import com.itellyou.model.event.AnswerEvent;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerVoteModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.VoteType;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.service.common.VoteService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.QuestionAnswerService;
import com.itellyou.service.question.QuestionAnswerSingleService;
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

@CacheConfig(cacheNames = "answer_vote")
@Service
public class QuestionAnswerVoteServiceImpl implements VoteService<QuestionAnswerVoteModel> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final QuestionAnswerVoteDao voteDao;
    private final QuestionAnswerService answerService;
    private final QuestionAnswerSingleService answerSingleService;
    private final OperationalPublisher operationalPublisher;
    private final VoteSearchService<QuestionAnswerVoteModel> voteSearchService;

    @Autowired
    public QuestionAnswerVoteServiceImpl(QuestionAnswerVoteDao voteDao, QuestionAnswerService answerService, QuestionAnswerSingleService answerSingleService, OperationalPublisher operationalPublisher, QuestionAnswerVoteSearchServiceImpl voteSearchService){
        this.voteDao = voteDao;
        this.answerService = answerService;
        this.answerSingleService = answerSingleService;
        this.operationalPublisher = operationalPublisher;
        this.voteSearchService = voteSearchService;
    }

    @Override
    public int insert(QuestionAnswerVoteModel voteModel) {
        return voteDao.insert(voteModel);
    }

    @Override
    @CacheEvict(key = "T(String).valueOf(#answerId).concat('-').concat(#userId)")
    public int deleteByTargetIdAndUserId(Long answerId, Long userId) {
        RedisUtils.clear("answer_vote_" + userId);
        return voteDao.deleteByAnswerIdAndUserId(answerId,userId);
    }

    @Override
    @Transactional
    @CacheEvict(key = "T(String).valueOf(#id).concat('-').concat(#userId)")
    public Map<String, Object> doVote(VoteType type, Long id, Long userId, Long ip) {
        try{
            QuestionAnswerVoteModel voteModel = voteSearchService.findByTargetIdAndUserId(id,userId);

            if(voteModel != null){
                int result = deleteByTargetIdAndUserId(id,userId);
                if(result != 1) throw new Exception("删除Vote失败");
                result = answerService.updateVote(voteModel.getType(),-1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }
            if(voteModel == null || !voteModel.getType().equals(type)){
                int result = insert(new QuestionAnswerVoteModel(id,type, DateUtils.getTimestamp(),userId, ip));
                if(result != 1) throw new Exception("写入Vote失败");
                result = answerService.updateVote(type,1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }

            QuestionAnswerModel commentModel = answerSingleService.findById(id);
            if(commentModel == null) throw new Exception("获取回答失败");
            if(commentModel.getCreatedUserId().equals((userId))) throw new Exception("不能给自己点赞");
            Map<String,Object> data = new HashMap<>();
            data.put("id",commentModel.getId());
            data.put("questionId",commentModel.getQuestionId());
            data.put("support",commentModel.getSupport());
            data.put("oppose",commentModel.getOppose());

            if(voteModel != null){
                operationalPublisher.publish(new AnswerEvent(this,
                        voteModel.getType().equals(VoteType.SUPPORT) ? EntityAction.UNLIKE : EntityAction.UNDISLIKE,
                        commentModel.getId(),commentModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
            }
            if(voteModel == null || !voteModel.getType().equals(type)){
                operationalPublisher.publish(new AnswerEvent(this,
                        type.equals(VoteType.SUPPORT) ? EntityAction.LIKE : EntityAction.DISLIKE,
                        commentModel.getId(),commentModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
            }
            RedisUtils.clear("answer_vote_" + userId);
            return data;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }
}
