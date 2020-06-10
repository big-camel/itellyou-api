package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerPaidReadDao;
import com.itellyou.model.question.QuestionAnswerPaidReadModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserStarDetailModel;
import com.itellyou.service.question.QuestionAnswerPaidReadSearchService;
import com.itellyou.service.question.QuestionSingleService;
import com.itellyou.service.user.bank.UserBankLogService;
import com.itellyou.service.user.star.UserStarSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@CacheConfig(cacheNames = "question_answer_paid_read")
@Service
public class QuestionAnswerPaidReadSearchServiceImpl implements QuestionAnswerPaidReadSearchService {

    private final QuestionAnswerPaidReadDao articlePaidReadDao;
    private final UserStarSearchService userStarService;
    private final UserBankLogService bankLogService;
    private final QuestionSingleService questionSingleService;

    public QuestionAnswerPaidReadSearchServiceImpl(QuestionAnswerPaidReadDao articlePaidReadDao, UserStarSearchService userStarService, UserBankLogService bankLogService, QuestionSingleService questionSingleService) {
        this.articlePaidReadDao = articlePaidReadDao;
        this.userStarService = userStarService;
        this.bankLogService = bankLogService;
        this.questionSingleService = questionSingleService;
    }

    @Override
    @Cacheable(key = "#answerId",unless = "#result == null")
    public QuestionAnswerPaidReadModel findByAnswerId(Long answerId) {
        return articlePaidReadDao.findByAnswerId(answerId);
    }

    @Override
    public boolean checkRead(QuestionAnswerPaidReadModel paidReadModel,Long questionId , Long authorId, Long userId) {
        if(paidReadModel != null && !authorId.equals(userId)){
            if(userId == null) return false;
            // 提问者有权限查看
            QuestionInfoModel questionInfoModel = questionSingleService.findById(questionId);
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
    public List<QuestionAnswerPaidReadModel> search(HashSet<Long> answerIds) {
        return RedisUtils.fetchByCache("question_answer_paid_read", QuestionAnswerPaidReadModel.class,answerIds,(HashSet<Long> fetchIds) ->
                articlePaidReadDao.search(fetchIds)
        );
    }
}
