package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionInfoDao;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.sys.RewardType;
import com.itellyou.service.question.QuestionSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "question")
@Service
public class QuestionSingleServiceImpl implements QuestionSingleService {

    private final QuestionInfoDao questionInfoDao;

    public QuestionSingleServiceImpl(QuestionInfoDao questionInfoDao) {
        this.questionInfoDao = questionInfoDao;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public QuestionInfoModel findById(Long id) {
        return questionInfoDao.findById(id);
    }

    @Override
    public List<QuestionInfoModel> search(HashSet<Long> ids, String mode, Long userId, Long searchUserId, Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long ip, RewardType rewardType, Double minRewardValue, Double maxRewardValue, Integer minComments, Integer maxComments, Integer minAnswers, Integer maxAnswers, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetchByCache("question", QuestionInfoModel.class,ids,(HashSet<Long> fetchIds) ->
                questionInfoDao.search(ids,mode,userId,searchUserId,isDisabled,isAdopted,isPublished,isDeleted,ip,rewardType,minRewardValue,maxRewardValue,minComments,maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit)
        );
    }
}
