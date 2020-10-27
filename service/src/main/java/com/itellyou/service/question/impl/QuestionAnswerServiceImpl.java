package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerDao;
import com.itellyou.model.common.DataUpdateStepModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.event.AnswerEvent;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.RewardType;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankType;
import com.itellyou.service.common.ViewService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.*;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.service.user.bank.UserBankService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
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

@Service
@CacheConfig(cacheNames = CacheKeys.QUESTION_ANSWER_KEY)
public class QuestionAnswerServiceImpl implements QuestionAnswerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionAnswerDao answerDao;
    private final QuestionAnswerSearchService answerSearchService;
    private final ViewService viewService;
    private final QuestionInfoService questionService;
    private final QuestionSingleService questionSingleService;
    private final QuestionAnswerSingleService answerSingleService;
    private final UserBankService bankService;
    private final UserInfoService userInfoService;
    private final UserDraftService draftService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public QuestionAnswerServiceImpl(QuestionAnswerDao answerDao, QuestionAnswerSearchService answerSearchService, ViewService viewService, QuestionInfoService questionService, QuestionSingleService questionSingleService, QuestionAnswerSingleService answerSingleService, UserBankService bankService, UserInfoService userInfoService, UserDraftService draftService, OperationalPublisher operationalPublisher){
        this.answerDao = answerDao;
        this.viewService = viewService;
        this.answerSearchService = answerSearchService;
        this.questionService = questionService;
        this.questionSingleService = questionSingleService;
        this.answerSingleService = answerSingleService;
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
    public int addStep(DataUpdateStepModel... models) {
        for (DataUpdateStepModel model : models) {
            RedisUtils.remove(CacheKeys.QUESTION_ANSWER_KEY,model.getId());
        }
        return answerDao.addStep(models);
    }

    @Override
    public int updateView(Long userId,Long id,Long ip,String os,String browser) {
        try{
            QuestionAnswerModel answerModel = answerSingleService.findById(id);
            if(answerModel == null) throw new Exception("错误的编号");
            long prevTime = viewService.insertOrUpdate(userId,EntityType.ANSWER,id,answerModel.getDescription(),ip,os,browser);

            if(DateUtils.getTimestamp() - prevTime > 3600){
                answerModel.setViewCount(answerModel.getViewCount() + 1);
                operationalPublisher.publish(new AnswerEvent(this,
                        EntityAction.VIEW,answerModel.getQuestionId(),null,answerModel.getId(),answerModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));
            }
            return answerModel.getViewCount();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            return 0;
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
    public boolean adopt(Long id, Long userId, String ip) throws Exception {
        try {
            QuestionAnswerModel answerModel = answerSingleService.findById(id);
            if(answerModel == null) throw new Exception("不存在的回答");
            if(answerModel.isAdopted() || answerModel.isDisabled() || answerModel.isDeleted() || !answerModel.isPublished()) throw new Exception("错误的回答状态");

            QuestionInfoModel detailModel = questionSingleService.findById(answerModel.getQuestionId());
            if(detailModel == null || !detailModel.getCreatedUserId().equals(userId)) throw new Exception("不存在的提问，或无权限");
            if(detailModel.isAdopted() || detailModel.isDisabled() || detailModel.isDeleted() || !detailModel.isPublished()) throw new Exception("错误的提问状态");

            int result = questionService.updateAdopt(true,answerModel.getId(),detailModel.getId());
            if(result != 1) throw new Exception("更新提问采纳状态失败");

            result = updateAdopted(true,id);
            if(result != 1) throw new Exception("更新回答采纳状态失败");
            Double rewardAmount = detailModel.getRewardType() != RewardType.DEFAULT ? detailModel.getRewardValue() : 0.00;
            if(rewardAmount > 0){
                UserBankLogModel logModel = bankService.update(rewardAmount,UserBankType.valueOf(detailModel.getRewardType().getValue()),EntityAction.ADOPT,EntityType.ANSWER,id.toString(),answerModel.getCreatedUserId(),"回答被采纳", IPUtils.toLong(ip));
                if(logModel == null){
                    throw new Exception("悬赏支付失败");
                }
            }
            AnswerEvent event = new AnswerEvent(this,
                    EntityAction.ADOPT,answerModel.getQuestionId(),detailModel.getCreatedUserId(),answerModel.getId(),answerModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),IPUtils.toLong(ip));
            event.setArgs(new HashMap<String, Object>(){{
                put("amount",rewardAmount);
            }});
            operationalPublisher.publish(event);
            return true;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public boolean delete(Long id,Long questionId, Long userId,Long ip) throws Exception {
        try{
            QuestionAnswerModel answerModel = answerSingleService.findById(id);
            if(answerModel == null || !answerModel.getCreatedUserId().equals(userId) || !answerModel.getQuestionId().equals(questionId)){
                throw new Exception("错误的回答Id");
            }
            if(answerModel.isDisabled() || answerModel.isAdopted() || answerModel.isDeleted()){
                throw new Exception("回答状态不可用");
            }
            QuestionInfoModel questionInfoModel = questionSingleService.findById(answerModel.getQuestionId());
            if(questionInfoModel == null) throw new Exception("问题不存在");

            int result = updateDeleted(true,id);
            if(result != 1) throw new Exception("更新回答删除状态失败");
            result = userInfoService.updateAnswerCount(userId,-1);
            if(result != 1) throw new Exception("更新用户回答数量失败");

            draftService.delete(userId,EntityType.ANSWER,id);

            operationalPublisher.publish(new AnswerEvent(this,
                    EntityAction.DELETE,answerModel.getQuestionId(),questionInfoModel.getCreatedUserId(),id,answerModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));

            return true;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public boolean revokeDelete(Long id, Long questionId, Long userId,Long ip) throws Exception {
        try{
            QuestionAnswerModel answerModel = answerSingleService.findById(id);
            if(answerModel == null || !answerModel.getCreatedUserId().equals(userId) || !answerModel.getQuestionId().equals(questionId)){
                throw new Exception("错误的回答Id");
            }
            if(answerModel.isDisabled() || !answerModel.isPublished() || !answerModel.isDeleted()){
                throw new Exception("回答状态不可用");
            }
            QuestionInfoModel questionInfoModel = questionSingleService.findById(answerModel.getQuestionId());
            if(questionInfoModel == null) throw new Exception("问题不存在");

            int result = updateDeleted(false,id);
            if(result != 1) throw new Exception("更新提问回答数量失败");
            result = userInfoService.updateAnswerCount(userId,1);
            if(result != 1) throw new Exception("更新用户回答数量失败");

            operationalPublisher.publish(new AnswerEvent(this,
                    EntityAction.REVERT,answerModel.getQuestionId(),questionInfoModel.getCreatedUserId(),id,answerModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));

            return true;

        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
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

    @Override
    @CacheEvict(key = "#id")
    public int updateInfo(Long id, String description, Long time, Long ip, Long userId) {
        return answerDao.updateInfo(id,description,time,ip,userId);
    }

}
