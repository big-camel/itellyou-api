package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerPaidReadDao;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.event.OperationalEvent;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionAnswerPaidReadModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankType;
import com.itellyou.model.user.UserStarDetailModel;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.QuestionAnswerPaidReadSearchService;
import com.itellyou.service.question.QuestionAnswerPaidReadService;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.service.user.UserBankLogService;
import com.itellyou.service.user.UserBankService;
import com.itellyou.service.user.UserStarService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@CacheConfig(cacheNames = "question_answer_paid_read")
@Service
public class QuestionAnswerPaidReadServiceImpl implements QuestionAnswerPaidReadService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionAnswerPaidReadDao answerPaidReadDao;
    private final QuestionAnswerPaidReadSearchService paidReadSearchService;
    private final UserStarService userStarService;
    private final UserBankLogService bankLogService;
    private final UserBankService userBankService;
    private final QuestionAnswerSearchService answerSearchService;
    private final OperationalPublisher operationalPublisher;
    private final QuestionSearchService questionSearchService;

    public QuestionAnswerPaidReadServiceImpl(QuestionAnswerPaidReadDao answerPaidReadDao, QuestionAnswerPaidReadSearchService paidReadSearchService, UserStarService userStarService, UserBankLogService bankLogService, UserBankService userBankService, QuestionAnswerSearchService answerSearchService, OperationalPublisher operationalPublisher, QuestionSearchService questionSearchService) {
        this.answerPaidReadDao = answerPaidReadDao;
        this.paidReadSearchService = paidReadSearchService;
        this.userStarService = userStarService;
        this.bankLogService = bankLogService;
        this.userBankService = userBankService;
        this.answerSearchService = answerSearchService;
        this.operationalPublisher = operationalPublisher;
        this.questionSearchService = questionSearchService;
    }

    @Override
    @CacheEvict
    public int insert(QuestionAnswerPaidReadModel model) {
        return answerPaidReadDao.insert(model);
    }

    @Override
    @Transactional
    @CacheEvict
    public int insertOrUpdate(QuestionAnswerPaidReadModel model) {
        try{
            QuestionAnswerPaidReadModel readModel = paidReadSearchService.findByAnswerId(model.getAnswerId());
            if (readModel != null) {
                int result = deleteByAnswerId(model.getAnswerId());
                if(result != 1) throw new Exception("删除失败");
            }
            int result = insert(model);
            if(result != 1) throw new Exception("设置失败");
            return 1;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    @CacheEvict(key = "#answerId")
    public int deleteByAnswerId(Long answerId) {
        return answerPaidReadDao.deleteByAnswerId(answerId);
    }

    @Override
    public boolean checkRead(QuestionAnswerPaidReadModel paidReadModel,Long questionId , Long authorId, Long userId) {
        if(paidReadModel != null && !authorId.equals(userId)){
            if(userId == null) return false;
            // 提问者有权限查看
            QuestionInfoModel questionInfoModel = questionSearchService.findById(questionId);
            if(questionInfoModel == null) return false;
            if(questionInfoModel.getCreatedUserId().equals(userId)) return true;
            // 如果设置了关注才能查看则判断是否关注
            if(paidReadModel.getStarToRead()){
                UserStarDetailModel starModel = userStarService.find(authorId,userId);
                if(starModel != null) return true;
            }
            // 如果设置了需要付费，则查询是否有付费记录
            if(paidReadModel.getPaidToRead()){
                List<UserBankLogModel> logModels = bankLogService.search(null,paidReadModel.getPaidType(), EntityAction.PAYMENT, EntityType.ANSWER,paidReadModel.getAnswerId().toString(),userId,null,null,null,null,null,null);
                return logModels != null && logModels.size() > 0 && logModels.get(0).getAmount() < 0;
            }else return false;
        }
        return true;
    }

    @Override
    @Transactional
    public UserBankLogModel doPaidRead(Long answerId, Long userId, Long ip) throws Exception {
        try{
            QuestionAnswerDetailModel detailModel = answerSearchService.getDetail(answerId);
            if(detailModel == null || detailModel.isDisabled() || detailModel.isDeleted() || !detailModel.isPublished()) throw  new Exception("回答不可用");

            QuestionAnswerPaidReadModel paidReadModel = detailModel.getPaidRead();

            Long targetUserId = detailModel.getCreatedUserId();
            if(targetUserId.equals(userId)) throw new Exception("不能给自己支付");

            Double amount = paidReadModel.getPaidAmount();

            UserBankLogModel bankLogModel = userBankService.update(-Math.abs(amount),paidReadModel.getPaidType(), EntityAction.PAYMENT,EntityType.ANSWER,answerId.toString(),userId,"购买付费阅读内容",ip);
            if(bankLogModel == null) throw new Exception("扣款失败");
            UserBankLogModel targetBankLogModel = userBankService.update(Math.abs(amount),paidReadModel.getPaidType(),EntityAction.PAYMENT,EntityType.ANSWER,answerId.toString(),targetUserId,"收到付费阅读内容付款",ip);
            if(targetBankLogModel == null) throw new Exception("收款失败");

            if(paidReadModel.getPaidType().equals(UserBankType.CASH)) {
                OperationalModel operationalModel = new OperationalModel(EntityAction.PAYMENT, EntityType.ANSWER, answerId, targetUserId, userId, DateUtils.getTimestamp(), ip);
                operationalPublisher.publish(new OperationalEvent(this, operationalModel));
            }
            return bankLogModel;
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }
}
