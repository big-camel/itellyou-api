package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionStarDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.event.QuestionEvent;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.question.QuestionStarDetailModel;
import com.itellyou.model.question.QuestionStarModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.common.StarService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.QuestionInfoService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.service.question.QuestionSingleService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = CacheKeys.QUESTION_STAR_KEY)
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
    @CacheEvict(key = "T(String).valueOf(#model.questionId).concat('-').concat(#model.createdUserId)")
    public int insert(QuestionStarModel model) throws Exception {
        QuestionInfoModel infoModel = questionSingleService.findById(model.getQuestionId());
        try{
            if(infoModel == null) throw new Exception("错误的问题ID");
            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入关注记录失败");
            operationalPublisher.publish(new QuestionEvent(this, EntityAction.FOLLOW,model.getQuestionId(),infoModel.getCreatedUserId(),model.getCreatedUserId(), DateUtils.toLocalDateTime(),model.getCreatedIp()));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            throw e;
        }
        return 1;
    }

    @Override
    @CacheEvict(key = "T(String).valueOf(#questionId).concat('-').concat(#userId)")
    public int delete(Long questionId, Long userId,Long ip) throws Exception {
        QuestionInfoModel infoModel = questionSingleService.findById(questionId);
        try{
            if(infoModel == null) throw new Exception("错误的问题ID");
            int result = starDao.delete(questionId,userId);
            if(result != 1) throw new Exception("删除关注记录失败");
            operationalPublisher.publish(new QuestionEvent(this, EntityAction.UNFOLLOW,questionId,infoModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));

        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            throw e;
        }
        return 1;
    }

    @Override
    public List<QuestionStarDetailModel> search(Collection<Long> questionIds, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionStarModel> starModels = starDao.search(questionIds,userId,beginTime,endTime,ip,order,offset,limit);

        List<QuestionStarDetailModel> detailModels = new ArrayList<>();
        if(starModels.size() == 0) return detailModels;
        Collection<Long> questionFetchIds = new LinkedHashSet<>();
        Collection<Long> userIds = new LinkedHashSet<>();

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
    public int count(Collection<Long> questionIds, Long userId, Long beginTime, Long endTime, Long ip) {
        return starDao.count(questionIds,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<QuestionStarDetailModel> page(Collection<Long> questionIds, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<QuestionStarDetailModel> data = search(questionIds,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(questionIds,userId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
