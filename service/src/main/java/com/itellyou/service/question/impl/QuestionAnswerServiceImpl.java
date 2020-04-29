package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerDao;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.question.*;
import com.itellyou.model.reward.RewardType;
import com.itellyou.model.user.*;
import com.itellyou.model.view.ViewInfoModel;
import com.itellyou.service.question.*;
import com.itellyou.service.user.UserBankService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.service.user.UserOperationalService;
import com.itellyou.service.view.ViewInfoService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionAnswerServiceImpl implements QuestionAnswerService {

    private final QuestionAnswerDao answerDao;
    private final QuestionAnswerVersionService versionService;
    private final QuestionAnswerSearchService answerSearchService;
    private final ViewInfoService viewService;
    private final QuestionAnswerVoteService voteService;
    private final QuestionInfoService questionService;
    private final QuestionSearchService questionSearchService;
    private final QuestionIndexService questionIndexService;
    private final UserBankService bankService;
    private final QuestionAnswerIndexService indexService;
    private final UserOperationalService operationalService;
    private final UserInfoService userInfoService;
    private final UserDraftService draftService;

    @Autowired
    public QuestionAnswerServiceImpl(QuestionAnswerDao answerDao, QuestionAnswerVersionService versionService, QuestionAnswerSearchService answerSearchService, ViewInfoService viewService, QuestionAnswerVoteService voteService, QuestionInfoService questionService, QuestionSearchService questionSearchService, QuestionIndexService questionIndexService, UserBankService bankService, QuestionAnswerIndexService indexService, UserOperationalService operationalService, UserInfoService userInfoService, UserDraftService draftService){
        this.answerDao = answerDao;
        this.viewService = viewService;
        this.versionService = versionService;
        this.answerSearchService = answerSearchService;
        this.voteService = voteService;
        this.questionService = questionService;
        this.questionSearchService = questionSearchService;
        this.questionIndexService = questionIndexService;
        this.bankService = bankService;
        this.indexService = indexService;
        this.operationalService = operationalService;
        this.userInfoService = userInfoService;
        this.draftService = draftService;
    }
    @Override
    public int insert(QuestionAnswerModel answerModel) {
        return answerDao.insert(answerModel);
    }


    @Override
    @Transactional
    public int updateView(Long userId,Long id,Long ip,String os,String browser) throws Exception {
        try{
            long prevTime = viewService.insertOrUpdate(userId,EntityType.ANSWER,id,ip,os,browser);

            if(DateUtils.getTimestamp() - prevTime > 3600){
                int result = answerDao.updateView(id,1);
                if(result != 1){
                    throw new Exception("更新浏览次数失败");
                }
                indexService.updateIndex(id);
            }
            indexService.updateIndex(id);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    public int updateComments(Long id, Integer value) {
        return answerDao.updateComments(id,value);
    }

    @Override
    public int updateDisabled(Boolean isDisabled, Long id) {
        return answerDao.updateDisabled(isDisabled,id);
    }

    @Override
    public int updateDeleted(Boolean isDeleted, Long id) {
        return answerDao.updateDeleted(isDeleted,id);
    }

    @Override
    public int updateAdopted(Boolean isAdopted, Long id) {
        return answerDao.updateAdopted(isAdopted,id);
    }

    @Override
    public int updateStarCountById(Long id, Integer step) {
        return answerDao.updateStarCountById(id,step);
    }

    @Override
    @Transactional
    public QuestionDetailModel adopt(Long id, Long userId, String ip) throws Exception {
        try {
            QuestionAnswerModel answerModel = answerSearchService.findById(id);
            if(answerModel == null) throw new Exception("不存在的回答");
            if(answerModel.isAdopted() || answerModel.isDisabled() || answerModel.isDeleted() || !answerModel.isPublished()) throw new Exception("错误的回答状态");

            QuestionDetailModel detailModel = questionSearchService.getDetail(answerModel.getQuestionId(),userId);
            if(detailModel == null) throw new Exception("不存在的提问");
            if(detailModel.isAdopted() || detailModel.isDisabled() || detailModel.isDeleted() || !detailModel.isPublished()) throw new Exception("错误的提问状态");

            int result = questionService.updateAdopt(true,answerModel.getId(),detailModel.getId());
            if(result != 1) throw new Exception("更新提问采纳状态失败");

            result = updateAdopted(true,id);
            if(result != 1) throw new Exception("更新回答采纳状态失败");
            if(detailModel.getRewardType() != RewardType.DEFAULT && detailModel.getRewardValue() > 0){
                UserBankLogModel logModel = bankService.update(detailModel.getRewardValue(),UserBankType.valueOf(detailModel.getRewardType().getValue()),answerModel.getCreatedUserId(),"回答[" + detailModel.getTitle() + "]被采纳", UserBankLogType.QUESTION_ANSWER,id.toString(), IPUtils.toLong(ip));
                if(logModel == null){
                    throw new Exception("悬赏支付失败");
                }
            }
            detailModel.setAdopted(true);
            questionIndexService.updateIndex(detailModel);
            return detailModel;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @Transactional
    public QuestionAnswerDetailModel delete(Long id,Long questionId, Long userId) throws Exception {
        try{
            QuestionAnswerModel answerModel = answerSearchService.findById(id);
            if(answerModel == null || !answerModel.getCreatedUserId().equals(userId) || !answerModel.getQuestionId().equals(questionId)){
                throw new Exception("错误的回答Id");
            }
            if(answerModel.isDisabled() || !answerModel.isPublished() || answerModel.isAdopted() || answerModel.isDeleted()){
                throw new Exception("回答状态不可用");
            }
            answerModel.setDeleted(true);
            int result = updateDeleted(true,id);
            if(result != 1) throw new Exception("更新回答删除状态失败");
            result = questionService.updateAnswers(questionId,-1);
            if(result != 1) throw new Exception("更新提问回答数量失败");
            result = userInfoService.updateAnswerCount(userId,-1);
            if(result != 1) throw new Exception("更新用户回答数量失败");
            questionIndexService.updateIndex(questionId);
            indexService.delete(id);
            draftService.delete(userId,EntityType.ANSWER,id);
            return answerSearchService.getDetail(id,questionId,userId,userId,true);
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @Transactional
    public QuestionAnswerDetailModel revokeDelete(Long id, Long questionId, Long userId) throws Exception {
        try{
            QuestionAnswerModel answerModel = answerSearchService.findById(id);
            if(answerModel == null || !answerModel.getCreatedUserId().equals(userId) || !answerModel.getQuestionId().equals(questionId)){
                throw new Exception("错误的回答Id");
            }
            if(answerModel.isDisabled() || !answerModel.isPublished() || !answerModel.isDeleted()){
                throw new Exception("回答状态不可用");
            }
            answerModel.setDeleted(false);
            int result = updateDeleted(false,id);
            if(result != 1) throw new Exception("更新回答删除状态失败");
            result = questionService.updateAnswers(questionId,1);
            if(result != 1) throw new Exception("更新提问回答数量失败");
            result = userInfoService.updateAnswerCount(userId,1);
            if(result != 1) throw new Exception("更新用户回答数量失败");
            questionIndexService.updateIndex(questionId);
            return answerSearchService.getDetail(id,questionId,userId,userId,true);
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @Transactional
    public Long create(Long questionId, Long userId, String content, String html,String description, String remark, String save_type, Long ip) throws Exception {

        try{
            QuestionAnswerModel answerModel = new QuestionAnswerModel();
            answerModel.setQuestionId(questionId);
            answerModel.setDraft(0);
            answerModel.setCreatedIp(ip);
            answerModel.setCreatedTime(DateUtils.getTimestamp());
            answerModel.setCreatedUserId(userId);
            int resultRows = insert(answerModel);
            if(resultRows != 1)
                throw new Exception("写入回答失败");
            QuestionAnswerVersionModel versionModel = versionService.addVersion(answerModel.getId(),questionId,userId,content,html,description,remark,1,save_type,ip,false,true);
            if(versionModel == null)
                throw new Exception("写入版本失败");
            return answerModel.getId();
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    @Transactional
    public Map<String,Object> updateVote(VoteType type, Long id, Long userId, String ip) {
        try{
            Map<String,Object> data = new HashMap<>();
            QuestionAnswerVoteModel voteModel = voteService.findByAnswerIdAndUserId(id,userId);
            if(voteModel != null){
                int result = voteService.deleteByAnswerIdAndUserId(id,userId);
                if(result != 1) throw new Exception("删除Vote失败");
                result = answerDao.updateVote(voteModel.getType(),-1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }
            if(voteModel == null || voteModel.getType() != type){
                voteModel = new QuestionAnswerVoteModel(id,type,DateUtils.getTimestamp(),userId, IPUtils.toLong(ip));
                int result = voteService.insert(voteModel);
                if(result != 1) throw new Exception("写入Vote失败");
                result = answerDao.updateVote(type,1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }

            QuestionAnswerModel answerModel = answerSearchService.findById(id);
            if(answerModel == null) throw new Exception("获取回答失败");
            if(type.equals(VoteType.SUPPORT)){
                operationalService.insertAsync(new UserOperationalModel(UserOperationalAction.LIKE, EntityType.ANSWER,answerModel.getId(),answerModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),IPUtils.toLong(ip)));
            }else{
                operationalService.deleteByTargetIdAsync(UserOperationalAction.LIKE, EntityType.ANSWER,userId,answerModel.getId());
            }
            indexService.updateIndex(id);
            data.put("id",answerModel.getId());
            data.put("support",answerModel.getSupport());
            data.put("oppose",answerModel.getOppose());
            return data;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    public int updateMetas(Long id, String cover) {
        return answerDao.updateMetas(id,cover);
    }

}
