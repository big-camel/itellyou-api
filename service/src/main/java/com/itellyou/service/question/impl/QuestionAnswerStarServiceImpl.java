package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerStarDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.event.AnswerEvent;
import com.itellyou.model.question.*;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.common.StarService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionAnswerSingleService;
import com.itellyou.service.question.QuestionSingleService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@CacheConfig(cacheNames = CacheKeys.QUESTION_ANSWER_STAR_KEY)
@Service
public class QuestionAnswerStarServiceImpl implements StarService<QuestionAnswerStarModel> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionAnswerStarDao starDao;
    private final QuestionAnswerSearchService answerSearchService;
    private final QuestionAnswerSingleService answerSingleService;
    private final UserInfoService userService;
    private final UserSearchService userSearchService;
    private final QuestionSingleService questionSingleService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public QuestionAnswerStarServiceImpl(QuestionAnswerStarDao starDao, QuestionAnswerSearchService answerSearchService, QuestionAnswerSingleService answerSingleService, UserInfoService userService, UserSearchService userSearchService, QuestionSingleService questionSingleService, OperationalPublisher operationalPublisher){
        this.starDao = starDao;
        this.answerSearchService = answerSearchService;
        this.answerSingleService = answerSingleService;
        this.userService = userService;
        this.userSearchService = userSearchService;
        this.questionSingleService = questionSingleService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    @CacheEvict(key = "T(String).valueOf(#model.answerId).concat('-').concat(#model.createdUserId)")
    public int insert(QuestionAnswerStarModel model) throws Exception {
        QuestionAnswerModel infoModel = answerSingleService.findById(model.getAnswerId());
        try{
            if(infoModel == null) throw new Exception("错误的回答ID");
            QuestionInfoModel questionInfoModel = questionSingleService.findById(infoModel.getQuestionId());
            if(questionInfoModel == null) throw new Exception("问题不存在");

            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入收藏记录失败");
            result = userService.updateCollectionCount(model.getCreatedUserId(),1);
            if(result != 1) throw new Exception("更新用户收藏数失败");

            operationalPublisher.publish(new AnswerEvent(this, EntityAction.FOLLOW,questionInfoModel.getId(),questionInfoModel.getCreatedUserId(),model.getAnswerId(),infoModel.getCreatedUserId(),model.getCreatedUserId(), DateUtils.toLocalDateTime(),model.getCreatedIp()));

        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        return infoModel.getStarCount() + 1;
    }

    @Override
    @Transactional
    @CacheEvict(key = "T(String).valueOf(#answerId).concat('-').concat(#userId)")
    public int delete(Long answerId, Long userId,Long ip) throws Exception {
        QuestionAnswerModel infoModel = answerSingleService.findById(answerId);
        try{
            if(infoModel == null) throw new Exception("错误的回答ID");
            QuestionInfoModel questionInfoModel = questionSingleService.findById(infoModel.getQuestionId());
            if(questionInfoModel == null) throw new Exception("问题不存在");

            int result = starDao.delete(answerId,userId);
            if(result != 1) throw new Exception("删除收藏记录失败");
            result = userService.updateCollectionCount(userId,-1);
            if(result != 1) throw new Exception("更新用户收藏数失败");
            operationalPublisher.publish(new AnswerEvent(this, EntityAction.UNFOLLOW,questionInfoModel.getId(),questionInfoModel.getCreatedUserId(),infoModel.getId(),infoModel.getCreatedUserId(),infoModel.getCreatedUserId(), DateUtils.toLocalDateTime(),infoModel.getCreatedIp()));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        return infoModel.getStarCount() - 1;
    }

    @Override
    public List<QuestionAnswerStarDetailModel> search(Collection<Long> answerId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionAnswerStarModel> starModels = starDao.search(answerId,userId,beginTime,endTime,ip,order,offset,limit);

        List<QuestionAnswerStarDetailModel> detailModels = new ArrayList<>();
        if(starModels.size() == 0) return detailModels;
        Collection<Long> answerIds = new LinkedHashSet<>();
        Collection<Long> userIds = new LinkedHashSet<>();

        for (QuestionAnswerStarModel starModel : starModels){
            QuestionAnswerStarDetailModel detailModel = new QuestionAnswerStarDetailModel(starModel);
            answerId.add(starModel.getAnswerId());
            userIds.add(starModel.getCreatedUserId());

            detailModels.add(detailModel);
        }

        // 一次获取回答
        List<QuestionAnswerDetailModel> answerDetailModels = answerSearchService.search(answerIds,null,null,userId,null,true,null,null,null,null,null,null,null,null,null);
        List<UserDetailModel> userDetailModels = userSearchService.search(userIds,userId,null,null,null,null,null,null,null,null,null,null);
        for (QuestionAnswerStarDetailModel detailModel : detailModels){
            for (QuestionAnswerDetailModel answerDetailModel :  answerDetailModels){
                if(answerDetailModel.getId().equals(detailModel.getAnswerId())){
                    detailModel.setAnswer(answerDetailModel);
                    break;
                }
            }
            // 设置对应的作者
            for (UserDetailModel userDetailModel : userDetailModels){
                if(detailModel.getCreatedUserId().equals(userDetailModel.getCreatedUserId())){
                    detailModel.setUser(userDetailModel);
                    break;
                }
            }
        }
        return detailModels;
    }

    @Override
    public int count(Collection<Long> answerId, Long userId, Long beginTime, Long endTime, Long ip) {
        return starDao.count(answerId,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<QuestionAnswerStarDetailModel> page(Collection<Long> answerId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<QuestionAnswerStarDetailModel> data = search(answerId,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(answerId,userId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
