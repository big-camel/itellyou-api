package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionCommentDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.event.OperationalEvent;
import com.itellyou.model.event.QuestionCommentEvent;
import com.itellyou.model.event.QuestionEvent;
import com.itellyou.model.question.QuestionCommentModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.VoteType;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.QuestionCommentService;
import com.itellyou.service.question.QuestionSingleService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = CacheKeys.QUESTION_COMMENT_KEY)
@Service
public class QuestionCommentServiceImpl implements QuestionCommentService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionCommentDao commentDao;
    private final QuestionSingleService questionSingleService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public QuestionCommentServiceImpl(QuestionCommentDao commentDao, QuestionSingleService questionSingleService, OperationalPublisher operationalPublisher){
        this.commentDao = commentDao;
        this.questionSingleService = questionSingleService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    public QuestionCommentModel insert(Long questionId, Long parentId, Long replyId, String content , String html, Long userId, String ip) throws Exception {
        try{
            QuestionInfoModel questionModel = questionSingleService.findById(questionId);
            if(questionModel == null) throw new Exception("错误的问题ID");
            QuestionCommentModel replyCommentModel = null;
            if(replyId != null && !replyId.equals(0l)){
                replyCommentModel = commentDao.findById(replyId);
                if(replyCommentModel == null) throw new Exception("错误的回复ID");
            }
            QuestionCommentModel commentModel = new QuestionCommentModel(null,questionId,parentId,replyId,false,content,html,0,0,0, DateUtils.toLocalDateTime(),userId, IPUtils.toLong(ip),null,null,null);
            int result = commentDao.insert(commentModel);

            if(result != 1) throw new Exception("写入评论失败");
            if(parentId != 0){
                result = updateComments(parentId,1);
                if(result != 1) throw new Exception("更新父级子评论数失败");
            }

            EntityType notificationType = EntityType.QUESTION;
            Long targetUserId = questionModel.getCreatedUserId();
            if(replyCommentModel != null){
                notificationType = EntityType.QUESTION_COMMENT;
                targetUserId = replyCommentModel.getCreatedUserId();
            }

            OperationalEvent event = notificationType.equals(EntityType.QUESTION) ?
                    new QuestionEvent(this, EntityAction.COMMENT,
                            commentModel.getId(),targetUserId,userId, DateUtils.toLocalDateTime(),IPUtils.toLong(ip)) :

                    new QuestionCommentEvent(this, EntityAction.COMMENT,commentModel.getId(),targetUserId,userId,DateUtils.toLocalDateTime(),IPUtils.toLong(ip));
            operationalPublisher.publish(event);

            return commentModel;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    public int updateDeleted(Long id, Boolean isDeleted,Long userId,Long ip) {
        QuestionCommentModel commentModel = commentDao.findById(id);
        if(commentModel == null) return 0;
        if(!commentModel.getCreatedUserId().equals(userId)) return 0;
        int result = commentDao.updateDeleted(id,isDeleted);
        if(result != 1) return 0;
        operationalPublisher.publish(new QuestionCommentEvent(this, isDeleted ? EntityAction.DELETE : EntityAction.REVERT,
                commentModel.getId(),commentModel.getCreatedUserId(),userId,DateUtils.toLocalDateTime(),ip));
        return 1;
    }

    @Override
    public int updateComments(Long id, Integer value) {
        return commentDao.updateComments(id,value);
    }

    @Override
    public int updateVote(VoteType type, Integer value, Long id) {
        return commentDao.updateVote(type,value,id);
    }
}
