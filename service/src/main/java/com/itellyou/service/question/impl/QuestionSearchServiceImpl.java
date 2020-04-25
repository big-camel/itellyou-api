package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionInfoDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.reward.RewardType;
import com.itellyou.service.question.QuestionSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuestionSearchServiceImpl implements QuestionSearchService {

    private final QuestionInfoDao questionInfoDao;

    @Autowired
    public QuestionSearchServiceImpl(QuestionInfoDao questionInfoDao){
        this.questionInfoDao = questionInfoDao;
    }

    @Override
    public List<QuestionDetailModel> search(HashSet<Long> ids, String mode, Long userId, Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long ip,
                                            Integer childCount,
                                            RewardType rewardType,
                                            Double minRewardValue,
                                            Double maxRewardValue, List<Long> tags, Integer minComments, Integer maxComments, Integer minAnswers, Integer maxAnswers, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return questionInfoDao.search(ids,mode,userId,searchUserId,hasContent,isDisabled,isAdopted,isPublished,isDeleted,ip,childCount,rewardType,minRewardValue,maxRewardValue,tags, minComments, maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(HashSet<Long> ids, String mode, Long userId, Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long ip,
                           RewardType rewardType,
                           Double minRewardValue,
                           Double maxRewardValue,List<Long> tags,Integer minComments,Integer maxComments, Integer minAnswers, Integer maxAnswers, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime) {
        return questionInfoDao.count(ids,mode,userId,isDisabled,isAdopted,isPublished,isDeleted,ip,rewardType,minRewardValue,maxRewardValue,tags,minComments,maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime);
    }

    @Override
    public List<QuestionDetailModel> search(HashSet<Long> ids, String mode, Long userId,Long searchUserId,Boolean hasContent,Integer childCount, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(ids,mode,userId,searchUserId,hasContent,null,null,null,null,null,childCount,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<QuestionDetailModel> search(Long searchUserId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished,Integer childCount,
                                            RewardType rewardType,
                                            Double minRewardValue,
                                            Double maxRewardValue,List<Long> tags,Integer minComments,Integer maxComments, Integer minAnswers, Integer maxAnswers, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,null,null,searchUserId,hasContent,isDisabled,isDeleted,isAdopted,isPublished,null,childCount,rewardType,minRewardValue,maxRewardValue,tags,minComments,maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<QuestionDetailModel> search(Long searchUserId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished,Integer childCount,
                                            RewardType rewardType,
                                            Double minRewardValue,
                                            Double maxRewardValue,List<Long> tags,Integer minComments,Integer maxComments, Integer minAnswers, Integer maxAnswers, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(searchUserId,hasContent,isDisabled,isDeleted,isAdopted,isPublished,childCount,rewardType,minRewardValue,maxRewardValue,tags,minComments,maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished,
                           RewardType rewardType,
                           Double minRewardValue,
                           Double maxRewardValue,List<Long> tags,Integer minComments,Integer maxComments, Integer minAnswers, Integer maxAnswers, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime) {
        return count(null,null,null,isDisabled,isDeleted,isAdopted,isPublished,null,rewardType,minRewardValue,maxRewardValue,tags,minComments,maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime);
    }

    @Override
    public List<QuestionDetailModel> search(Long searchUserId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(searchUserId,hasContent,isDisabled,isDeleted,isAdopted,isPublished,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<QuestionDetailModel> search(Long searchUserId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(searchUserId,hasContent,isDisabled,isDeleted,isAdopted,isPublished,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long beginTime, Long endTime) {
        return count(isDisabled,isDeleted,isAdopted,isPublished,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime);
    }

    @Override
    public PageModel<QuestionDetailModel> page(Long searchUserId,Long userId,Boolean hasContent,Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Integer childCount, RewardType rewardType, Double minRewardValue, Double maxRewardValue, List<Long> tags,Integer minComments, Integer maxComments, Integer minAnswers, Integer maxAnswers, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order,Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<QuestionDetailModel> data = search(null,null,userId,searchUserId,hasContent,isDisabled,isDeleted,isAdopted,isPublished,null,childCount,rewardType,minRewardValue,maxRewardValue,tags,minComments,maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);
        Integer total = count(null,null,userId,isDisabled,isDeleted,isAdopted,isPublished,null,rewardType,minRewardValue,maxRewardValue,tags,minComments,maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public PageModel<QuestionDetailModel> page(Long searchUserId,Long userId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long beginTime, Long endTime, Integer offset, Integer limit) {
        return page(searchUserId,userId,hasContent,isDisabled,isDeleted,isAdopted,isPublished,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,null,offset,limit);
    }

    @Override
    public QuestionDetailModel getDetail(Long id, String mode, Long userId) {
        List<QuestionDetailModel> listQuestion = search(new HashSet<Long>(){{add(id);}},mode,userId,null,null,null,null,null,null,0,1);
        return listQuestion != null && listQuestion.size() > 0 ? listQuestion.get(0) : null;
    }

    @Override
    public QuestionDetailModel getDetail(Long id, String mode) {
        return getDetail(id,mode,null);
    }

    @Override
    public QuestionDetailModel getDetail(Long id, Long userId, Long searchUserId) {
        List<QuestionDetailModel> listQuestion = search(new HashSet<Long>(){{add(id);}},null,userId,searchUserId,null,null,null,null,null,0,1);
        return listQuestion != null && listQuestion.size() > 0 ? listQuestion.get(0) : null;
    }

    @Override
    public QuestionDetailModel getDetail(Long id) {
        return getDetail(id,(String) null);
    }

    @Override
    public QuestionInfoModel findById(Long id) {
        return questionInfoDao.findById(id);
    }

    @Override
    public QuestionDetailModel getDetail(Long id, Long userId) {
        return getDetail(id,(String)null,userId);
    }

}
