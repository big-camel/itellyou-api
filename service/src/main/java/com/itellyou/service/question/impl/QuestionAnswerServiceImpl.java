package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerDao;
import com.itellyou.model.event.AnswerEvent;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerVersionModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.RewardType;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankType;
import com.itellyou.service.common.ViewService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.*;
import com.itellyou.service.user.UserBankService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@CacheConfig(cacheNames = "question_answer")
public class QuestionAnswerServiceImpl implements QuestionAnswerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionAnswerDao answerDao;
    private final QuestionAnswerVersionService versionService;
    private final QuestionAnswerSearchService answerSearchService;
    private final ViewService viewService;
    private final QuestionInfoService questionService;
    private final QuestionSearchService questionSearchService;
    private final UserBankService bankService;
    private final UserInfoService userInfoService;
    private final UserDraftService draftService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public QuestionAnswerServiceImpl(QuestionAnswerDao answerDao, QuestionAnswerVersionService versionService, QuestionAnswerSearchService answerSearchService, ViewService viewService, QuestionInfoService questionService, QuestionSearchService questionSearchService, UserBankService bankService, UserInfoService userInfoService, UserDraftService draftService, OperationalPublisher operationalPublisher){
        this.answerDao = answerDao;
        this.viewService = viewService;
        this.versionService = versionService;
        this.answerSearchService = answerSearchService;
        this.questionService = questionService;
        this.questionSearchService = questionSearchService;
        this.bankService = bankService;
        this.userInfoService = userInfoService;
        this.draftService = draftService;
        this.operationalPublisher = operationalPublisher;
    }
    @Override
    public int insert(QuestionAnswerModel answerModel) {
        return answerDao.insert(answerModel);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public int updateView(Long userId,Long id,Long ip,String os,String browser) throws Exception {
        try{
            QuestionAnswerModel answerModel = answerSearchService.findById(id);
            if(answerModel == null) throw new Exception("错误的编号");
            long prevTime = viewService.insertOrUpdate(userId,EntityType.ANSWER,id,ip,os,browser);

            if(DateUtils.getTimestamp() - prevTime > 3600){
                int result = answerDao.updateView(id,1);
                if(result != 1){
                    throw new Exception("更新回答浏览次数失败");
                }
                operationalPublisher.publish(new AnswerEvent(this,
                        EntityAction.VIEW,answerModel.getId(),answerModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateComments(Long id, Integer value) {
        return answerDao.updateComments(id,value);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateDisabled(Boolean isDisabled, Long id) {
        return answerDao.updateDisabled(isDisabled,id);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateDeleted(Boolean isDeleted, Long id) {
        return answerDao.updateDeleted(isDeleted,id);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateAdopted(Boolean isAdopted, Long id) {
        return answerDao.updateAdopted(isAdopted,id);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateStarCountById(Long id, Integer step) {
        return answerDao.updateStarCountById(id,step);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
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
                UserBankLogModel logModel = bankService.update(detailModel.getRewardValue(),UserBankType.valueOf(detailModel.getRewardType().getValue()),EntityAction.ADOPT,EntityType.ANSWER,id.toString(),answerModel.getCreatedUserId(),"回答被采纳", IPUtils.toLong(ip));
                if(logModel == null){
                    throw new Exception("悬赏支付失败");
                }
            }
            detailModel.setAdopted(true);
            operationalPublisher.publish(new AnswerEvent(this,
                    EntityAction.ADOPT,answerModel.getId(),answerModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),IPUtils.toLong(ip)));
            return detailModel;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public QuestionAnswerDetailModel delete(Long id,Long questionId, Long userId,Long ip) throws Exception {
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

            draftService.delete(userId,EntityType.ANSWER,id);
            QuestionAnswerDetailModel detailModel = answerSearchService.getDetail(id,questionId,userId,userId,true);

            operationalPublisher.publish(new AnswerEvent(this,
                    EntityAction.DELETE,id,answerModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));

            return detailModel;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public QuestionAnswerDetailModel revokeDelete(Long id, Long questionId, Long userId,Long ip) throws Exception {
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

            QuestionAnswerDetailModel detailModel = answerSearchService.getDetail(id,questionId,userId,userId,true);

            operationalPublisher.publish(new AnswerEvent(this,
                    EntityAction.REVERT,id,answerModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));

            return detailModel;

        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
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
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateVote(VoteType type, Integer value, Long id) {
        return answerDao.updateVote(type,value,id);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateMetas(Long id, String cover) {
        return answerDao.updateMetas(id,cover);
    }

}
