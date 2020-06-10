package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerCommentDao;
import com.itellyou.model.event.AnswerCommentEvent;
import com.itellyou.model.event.AnswerEvent;
import com.itellyou.model.event.OperationalEvent;
import com.itellyou.model.question.QuestionAnswerCommentModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.VoteType;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.QuestionAnswerCommentService;
import com.itellyou.service.question.QuestionAnswerService;
import com.itellyou.service.question.QuestionAnswerSingleService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = "question_answer_comment")
@Service
public class QuestionAnswerCommentServiceImpl implements QuestionAnswerCommentService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionAnswerCommentDao commentDao;
    private final QuestionAnswerService answerService;
    private final QuestionAnswerSingleService answerSingleService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public QuestionAnswerCommentServiceImpl(QuestionAnswerCommentDao commentDao, QuestionAnswerService answerService,  QuestionAnswerSingleService answerSingleService, OperationalPublisher operationalPublisher){
        this.commentDao = commentDao;
        this.answerService = answerService;
        this.answerSingleService = answerSingleService;
        this.operationalPublisher = operationalPublisher;
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
            QuestionAnswerCommentModel commentModel = new QuestionAnswerCommentModel(null,answerId,parentId,replyId,false,content,html,0,0,0, DateUtils.getTimestamp(),userId, ip,null,null,null);
            int result = commentDao.insert(commentModel);

            if(result != 1) throw new Exception("写入评论失败");
            if(parentId != 0){
                result = updateComments(parentId,1);
                if(result != 1) throw new Exception("更新父级子评论数失败");
            }
            result = answerService.updateComments(answerId,1);
            if(result != 1) throw new Exception("更新回答评论数失败");

            EntityType notificationType = EntityType.ANSWER;
            Long targetUserId = answerModel.getCreatedUserId();
            if(replyCommentModel != null){
                notificationType = EntityType.ANSWER_COMMENT;
                targetUserId = replyCommentModel.getCreatedUserId();
            }
            if(sendEvent == true) {
                OperationalEvent event = notificationType.equals(EntityType.ANSWER) ?

                        new AnswerEvent(this, EntityAction.COMMENT,
                                commentModel.getId(), targetUserId, userId, DateUtils.getTimestamp(), ip) :

                        new AnswerCommentEvent(this, EntityAction.COMMENT, commentModel.getId(), targetUserId, userId, DateUtils.getTimestamp(), ip);
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
