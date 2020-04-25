package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionCommentDao;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.question.*;
import com.itellyou.model.user.UserOperationalAction;
import com.itellyou.model.user.UserOperationalModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.question.*;
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
public class QuestionCommentServiceImpl implements QuestionCommentService {

    private final QuestionCommentDao commentDao;
    private final QuestionInfoService questionInfoService;
    private final QuestionSearchService questionSearchService;
    private final QuestionIndexService questionIndexService;
    private final QuestionCommentVoteService voteService;
    private final QuestionCommentSearchService commentSearchService;
    private final UserOperationalService operationalService;

    @Autowired
    public QuestionCommentServiceImpl(QuestionCommentDao commentDao,QuestionInfoService questionInfoService, QuestionSearchService questionSearchService,QuestionIndexService questionIndexService, QuestionCommentVoteService voteService,QuestionCommentSearchService commentSearchService, UserOperationalService operationalService){
        this.commentDao = commentDao;
        this.questionInfoService = questionInfoService;
        this.questionSearchService = questionSearchService;
        this.questionIndexService = questionIndexService;
        this.voteService = voteService;
        this.commentSearchService = commentSearchService;
        this.operationalService = operationalService;
    }

    @Override
    @Transactional
    public QuestionCommentModel insert(Long questionId, Long parentId, Long replyId, String content , String html, Long userId, String ip) throws Exception {
        try{
            QuestionInfoModel questionModel = questionSearchService.findById(questionId);
            if(questionModel == null) throw new Exception("错误的问题ID");
            QuestionCommentModel replyCommentModel = null;
            if(replyId != null && !replyId.equals(0l)){
                replyCommentModel = commentDao.findById(replyId);
                if(replyCommentModel == null) throw new Exception("错误的回复ID");
            }
            QuestionCommentModel commentModel = new QuestionCommentModel(null,questionId,parentId,replyId,false,content,html,0,0,0, DateUtils.getTimestamp(),userId, IPUtils.toLong(ip),null,null,null);
            int result = commentDao.insert(commentModel);

            if(result != 1) throw new Exception("写入评论失败");
            if(parentId != 0){
                result = updateComments(parentId,1);
                if(result != 1) throw new Exception("更新父级子评论数失败");
            }
            result = questionInfoService.updateComments(questionId,1);
            if(result != 1) throw new Exception("更新回答评论数失败");

            questionIndexService.updateIndex(questionId);

            EntityType notificationType = EntityType.QUESTION;
            Long targetUserId = questionModel.getCreatedUserId();
            if(replyCommentModel != null){
                notificationType = EntityType.QUESTION_COMMENT;
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
            QuestionCommentVoteModel voteModel = voteService.findByCommentIdAndUserId(id,userId);
            if(voteModel != null){
                int result = voteService.deleteByCommentIdAndUserId(id,userId);
                if(result != 1) throw new Exception("删除Vote失败");
                result = commentDao.updateVote(voteModel.getType(),-1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }
            if(voteModel == null || voteModel.getType() != type){
                voteModel = new QuestionCommentVoteModel(id,type,DateUtils.getTimestamp(),userId, IPUtils.toLong(ip));
                int result = voteService.insert(voteModel);
                if(result != 1) throw new Exception("写入Vote失败");
                result = commentDao.updateVote(type,1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }
            QuestionCommentModel commentModel = commentSearchService.findById(id);
            if(commentModel == null) throw new Exception("获取回答失败");
            if(commentModel.getCreatedUserId().equals((userId))) throw new Exception("不能给自己点赞");
            if(type.equals(VoteType.SUPPORT)){
                operationalService.insertAsync(new UserOperationalModel(UserOperationalAction.LIKE, EntityType.QUESTION_COMMENT,commentModel.getId(),commentModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),IPUtils.toLong(ip)));
            }else{
                operationalService.deleteByTargetIdAsync(UserOperationalAction.LIKE, EntityType.QUESTION_COMMENT,userId,commentModel.getId());
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
