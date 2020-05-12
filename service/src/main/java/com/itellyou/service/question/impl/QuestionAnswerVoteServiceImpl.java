package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerVoteDao;
import com.itellyou.model.event.AnswerEvent;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerVoteModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.VoteType;
import com.itellyou.service.common.VoteService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionAnswerService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.Map;

@Service
public class QuestionAnswerVoteServiceImpl implements VoteService<QuestionAnswerVoteModel> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final QuestionAnswerVoteDao voteDao;
    private final QuestionAnswerService answerService;
    private final QuestionAnswerSearchService searchService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public QuestionAnswerVoteServiceImpl(QuestionAnswerVoteDao voteDao, QuestionAnswerService answerService, QuestionAnswerSearchService searchService, OperationalPublisher operationalPublisher){
        this.voteDao = voteDao;
        this.answerService = answerService;
        this.searchService = searchService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    public int insert(QuestionAnswerVoteModel voteModel) {
        return voteDao.insert(voteModel);
    }

    @Override
    public int deleteByTargetIdAndUserId(Long answerId, Long userId) {
        return voteDao.deleteByAnswerIdAndUserId(answerId,userId);
    }

    @Override
    public QuestionAnswerVoteModel findByTargetIdAndUserId(Long answerId, Long userId) {
        return voteDao.findByAnswerIdAndUserId(answerId,userId);
    }

    @Override
    @Transactional
    public Map<String, Object> doVote(VoteType type, Long id, Long userId, Long ip) {
        try{
            QuestionAnswerVoteModel voteModel = findByTargetIdAndUserId(id,userId);

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

            QuestionAnswerModel commentModel = searchService.findById(id);
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
            return data;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }
}
