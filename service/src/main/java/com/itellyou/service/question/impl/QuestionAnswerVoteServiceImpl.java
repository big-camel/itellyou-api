package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerVoteDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.event.AnswerEvent;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerVoteModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.VoteType;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.service.common.VoteService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.QuestionAnswerSingleService;
import com.itellyou.service.question.QuestionSingleService;
import com.itellyou.util.DateUtils;
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

@CacheConfig(cacheNames = CacheKeys.ANSWER_VOTE_KEY)
@Service
public class QuestionAnswerVoteServiceImpl implements VoteService<QuestionAnswerVoteModel> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final QuestionAnswerVoteDao voteDao;
    private final QuestionAnswerSingleService answerSingleService;
    private final OperationalPublisher operationalPublisher;
    private final VoteSearchService<QuestionAnswerVoteModel> voteSearchService;
    private final QuestionSingleService questionSingleService;

    @Autowired
    public QuestionAnswerVoteServiceImpl(QuestionAnswerVoteDao voteDao, QuestionAnswerSingleService answerSingleService, OperationalPublisher operationalPublisher, QuestionAnswerVoteSearchServiceImpl voteSearchService, QuestionSingleService questionSingleService){
        this.voteDao = voteDao;
        this.answerSingleService = answerSingleService;
        this.operationalPublisher = operationalPublisher;
        this.voteSearchService = voteSearchService;
        this.questionSingleService = questionSingleService;
    }

    @Override
    public int insert(QuestionAnswerVoteModel voteModel) {
        return voteDao.insert(voteModel);
    }

    @Override
    @CacheEvict(key = "T(String).valueOf(#answerId).concat('-').concat(#userId)")
    public int deleteByTargetIdAndUserId(Long answerId, Long userId) {
        return voteDao.deleteByAnswerIdAndUserId(answerId,userId);
    }

    @Override
    @Transactional
    @CacheEvict(key = "T(String).valueOf(#id).concat('-').concat(#userId)")
    public Map<String, Object> doVote(VoteType type, Long id, Long userId, Long ip) {
        try{
            QuestionAnswerVoteModel voteModel = voteSearchService.findByTargetIdAndUserId(id,userId);
            QuestionAnswerModel answerModel = answerSingleService.findById(id);
            if(answerModel == null) throw new Exception("获取回答失败");
            if(answerModel.getCreatedUserId().equals((userId))) throw new Exception("不能给自己点赞");

            QuestionInfoModel questionInfoModel = questionSingleService.findById(answerModel.getQuestionId());
            if(questionInfoModel == null) throw new Exception("问题不存在");

            if(voteModel != null){
                int result = deleteByTargetIdAndUserId(id,userId);
                if(result != 1) throw new Exception("删除Vote失败");
            }
            if(voteModel == null || !voteModel.getType().equals(type)){
                int result = insert(new QuestionAnswerVoteModel(id,type, DateUtils.toLocalDateTime(),userId, ip));
                if(result != 1) throw new Exception("写入Vote失败");
            }

            int supportStep = 0;
            int opposeStep = 0;
            if(voteModel != null){
                if(voteModel.getType().equals(VoteType.SUPPORT)) supportStep = -1;
                else opposeStep = -1;
            }

            if(voteModel == null || !voteModel.getType().equals(type)){
                if(type.equals(VoteType.SUPPORT)) supportStep = 1;
                else  opposeStep = 1;
            }

            Map<String,Object> data = new HashMap<>();
            data.put("id",answerModel.getId());
            data.put("questionId",answerModel.getQuestionId());
            data.put("supportCount",answerModel.getSupportCount() + supportStep);
            data.put("opposeCount",answerModel.getOpposeCount() + opposeStep);

            if(voteModel != null){
                operationalPublisher.publish(new AnswerEvent(this,
                        voteModel.getType().equals(VoteType.SUPPORT) ? EntityAction.UNLIKE : EntityAction.UNDISLIKE,questionInfoModel.getId(),questionInfoModel.getCreatedUserId(),
                        answerModel.getId(),answerModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));
            }
            if(voteModel == null || !voteModel.getType().equals(type)){
                operationalPublisher.publish(new AnswerEvent(this,
                        type.equals(VoteType.SUPPORT) ? EntityAction.LIKE : EntityAction.DISLIKE,questionInfoModel.getId(),questionInfoModel.getCreatedUserId(),
                        answerModel.getId(),answerModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));
            }
            return data;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }
}
