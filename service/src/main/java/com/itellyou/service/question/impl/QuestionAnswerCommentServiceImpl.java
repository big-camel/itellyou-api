package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerCommentDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.event.AnswerCommentEvent;
import com.itellyou.model.event.AnswerEvent;
import com.itellyou.model.event.OperationalEvent;
import com.itellyou.model.question.QuestionAnswerCommentModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.VoteType;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.QuestionAnswerCommentService;
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

@CacheConfig(cacheNames = CacheKeys.QUESTION_ANSWER_COMMENT_KEY)
@Service
public class QuestionAnswerCommentServiceImpl implements QuestionAnswerCommentService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionAnswerCommentDao commentDao;
    private final QuestionAnswerSingleService answerSingleService;
    private final OperationalPublisher operationalPublisher;
    private final QuestionSingleService questionSingleService;

    @Autowired
    public QuestionAnswerCommentServiceImpl(QuestionAnswerCommentDao commentDao, QuestionAnswerSingleService answerSingleService, OperationalPublisher operationalPublisher, QuestionSingleService questionSingleService){
        this.commentDao = commentDao;
        this.answerSingleService = answerSingleService;
        this.operationalPublisher = operationalPublisher;
        this.questionSingleService = questionSingleService;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#parentId")
    public QuestionAnswerCommentModel insert(Long answerId,Long parentId,Long replyId,String content , String html,Long userId,Long ip,Boolean sendEvent) throws Exception {
        try{
            QuestionAnswerModel answerModel = answerSingleService.findById(answerId);
            if(answerModel == null) throw new Exception("错误的回答ID");
            QuestionAnswerCommentModel replyCommentModel = null;
            if(replyId != null && !replyId.equals(0l)){
                replyCommentModel = commentDao.findById(replyId);
                if(replyCommentModel == null) throw new Exception("错误的回复ID");
            }
            QuestionInfoModel questionInfoModel = questionSingleService.findById(answerModel.getQuestionId());
            if(questionInfoModel == null) throw new Exception("问题不存在");

            QuestionAnswerCommentModel commentModel = new QuestionAnswerCommentModel(null,answerId,parentId,replyId,false,content,html,0,0,0, DateUtils.toLocalDateTime(),userId, ip,null,null,null);
            int result = commentDao.insert(commentModel);

            if(result != 1) throw new Exception("写入评论失败");
            if(parentId != 0){
                result = updateComments(parentId,1);
                if(result != 1) throw new Exception("更新父级子评论数失败");
            }

            EntityType notificationType = EntityType.ANSWER;
            Long targetUserId = answerModel.getCreatedUserId();
            if(replyCommentModel != null){
                notificationType = EntityType.ANSWER_COMMENT;
                targetUserId = replyCommentModel.getCreatedUserId();
            }
            if(sendEvent == true) {
                OperationalEvent event = notificationType.equals(EntityType.ANSWER) ?

                        new AnswerEvent(this, EntityAction.COMMENT,answerModel.getQuestionId(),questionInfoModel.getCreatedUserId(),
                                commentModel.getId(), targetUserId, userId, DateUtils.toLocalDateTime(), ip) :

                        new AnswerCommentEvent(this, EntityAction.COMMENT, commentModel.getId(), targetUserId, userId, DateUtils.toLocalDateTime(), ip);
                event.setArgs(new HashMap<String, Object>(){{ put("answer_id",answerId);}});
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
        QuestionAnswerCommentModel commentModel = commentDao.findById(id);
        if(commentModel == null) return 0;
        if(!commentModel.getCreatedUserId().equals(userId)) return 0;
        int result = commentDao.updateDeleted(id,isDeleted);
        if(result != 1) return 0;
        operationalPublisher.publish(new AnswerCommentEvent(this, isDeleted ? EntityAction.DELETE : EntityAction.REVERT,
                commentModel.getId(),commentModel.getCreatedUserId(),userId,DateUtils.toLocalDateTime(),ip));
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
