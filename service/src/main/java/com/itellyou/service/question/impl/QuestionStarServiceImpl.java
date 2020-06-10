package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionStarDao;
import com.itellyou.model.question.*;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.event.QuestionEvent;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.common.StarService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.QuestionInfoService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.service.question.QuestionSingleService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@CacheConfig(cacheNames = "question_star")
@Service
public class QuestionStarServiceImpl implements StarService<QuestionStarModel> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionStarDao starDao;
    private final QuestionInfoService infoService;
    private final QuestionSearchService searchService;
    private final QuestionSingleService questionSingleService;
    private final UserSearchService userSearchService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public QuestionStarServiceImpl(QuestionStarDao starDao, QuestionInfoService infoService, QuestionSearchService searchService, QuestionSingleService questionSingleService, UserSearchService userSearchService, OperationalPublisher operationalPublisher){
        this.starDao = starDao;
        this.infoService = infoService;
        this.searchService = searchService;
        this.questionSingleService = questionSingleService;
        this.userSearchService = userSearchService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    @CacheEvict(key = "T(String).valueOf(#model.questionId).concat('-').concat(#model.createdUserId)")
    public int insert(QuestionStarModel model) throws Exception {
        QuestionInfoModel infoModel = questionSingleService.findById(model.getQuestionId());
        try{
            if(infoModel == null) throw new Exception("错误的问题ID");
            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入关注记录失败");
            result = infoService.updateStarCountById(model.getQuestionId(),1);
            if(result != 1) throw new Exception("更新关注数失败");
            operationalPublisher.publish(new QuestionEvent(this, EntityAction.FOLLOW,model.getQuestionId(),infoModel.getCreatedUserId(),model.getCreatedUserId(), DateUtils.getTimestamp(),model.getCreatedIp()));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        RedisUtils.clear("question_star_" + model.getCreatedUserId());
        return 1;
    }

    @Override
    @Transactional
    @CacheEvict(key = "T(String).valueOf(#questionId).concat('-').concat(#userId)")
    public int delete(Long questionId, Long userId,Long ip) throws Exception {
        QuestionInfoModel infoModel = questionSingleService.findById(questionId);
        try{
            if(infoModel == null) throw new Exception("错误的问题ID");
            int result = starDao.delete(questionId,userId);
            if(result != 1) throw new Exception("删除关注记录失败");
            result = infoService.updateStarCountById(questionId,-1);
            if(result != 1) throw new Exception("更新关注数失败");
            operationalPublisher.publish(new QuestionEvent(this, EntityAction.UNFOLLOW,questionId,infoModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));

        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        RedisUtils.clear("question_star_" + userId);
        return 1;
    }

    @Override
    public List<QuestionStarDetailModel> search(HashSet<Long> questionIds, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionStarModel> starModels = starDao.search(questionIds,userId,beginTime,endTime,ip,order,offset,limit);

        List<QuestionStarDetailModel> detailModels = new ArrayList<>();

        HashSet<Long> questionFetchIds = new LinkedHashSet<>();
        HashSet<Long> userIds = new LinkedHashSet<>();

        for (QuestionStarModel starModel : starModels){
            QuestionStarDetailModel detailModel = new QuestionStarDetailModel(starModel);
            questionFetchIds.add(starModel.getQuestionId());
            userIds.add(starModel.getCreatedUserId());

            detailModels.add(detailModel);
        }

        // 一次获取问题
        List<QuestionDetailModel> questionDetailModels = searchService.search(questionFetchIds,null,null,null,null,true,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null
        ,null,null,null,null,null,null,null,null,null,null);
        List<UserDetailModel> userDetailModels = userSearchService.search(userIds,null,null,null,null,null,null,null,null,null,null,null);
        for (QuestionStarDetailModel detailModel : detailModels){
            for (QuestionDetailModel questionDetailModel :  questionDetailModels){
                if(questionDetailModel.getId().equals(detailModel.getQuestionId())){
                    detailModel.setQuestion(questionDetailModel);
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
    public int count(HashSet<Long> questionIds, Long userId, Long beginTime, Long endTime, Long ip) {
        return starDao.count(questionIds,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<QuestionStarDetailModel> page(HashSet<Long> questionIds, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<QuestionStarDetailModel> data = search(questionIds,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(questionIds,userId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
