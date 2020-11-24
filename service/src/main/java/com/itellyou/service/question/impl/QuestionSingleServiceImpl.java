package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionInfoDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.question.QuestionUpdateStepModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.RewardType;
import com.itellyou.service.common.DataUpdateQueueService;
import com.itellyou.service.question.QuestionSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = CacheKeys.QUESTION_KEY)
@Service
public class QuestionSingleServiceImpl implements QuestionSingleService {

    private final QuestionInfoDao questionInfoDao;
    private final DataUpdateQueueService updateQueueService;

    public QuestionSingleServiceImpl(QuestionInfoDao questionInfoDao, DataUpdateQueueService updateQueueService) {
        this.questionInfoDao = questionInfoDao;
        this.updateQueueService = updateQueueService;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public QuestionInfoModel findById(Long id) {
        return questionInfoDao.findById(id);
    }

    @Override
    public List<QuestionInfoModel> search(Collection<Long> ids, String mode, Long userId, Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long ip, RewardType rewardType, Double minRewardValue, Double maxRewardValue, Integer minComment, Integer maxComment, Integer minAnswer, Integer maxAnswer, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionInfoModel> infoModels = RedisUtils.fetch(CacheKeys.QUESTION_KEY, QuestionInfoModel.class,ids,(Collection<Long> fetchIds) ->
                questionInfoDao.search(ids,mode,userId,isDisabled,isAdopted,isPublished,isDeleted,ip,rewardType,minRewardValue,maxRewardValue,minComment,maxComment,minAnswer,maxAnswer,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit)
        );
        // 从缓存里面计算统计数据值
        List<QuestionUpdateStepModel> stepModels = updateQueueService.get(EntityType.QUESTION,infoModels.stream().map(QuestionInfoModel::getId).collect(Collectors.toSet()),(stepModel,model) -> {
            updateQueueService.cumulative(stepModel,model);
            stepModel.setAnswerStep(stepModel.getAnswerStep() + model.getAnswerStep());
        });
        infoModels.forEach(model -> {
            stepModels.stream().filter(stepModel -> stepModel.getId().equals(model.getId())).findFirst().ifPresent(stepModel -> {
                model.setViewCount(model.getViewCount() + stepModel.getViewStep());
                model.setSupportCount(model.getSupportCount() + stepModel.getSupportStep());
                model.setOpposeCount(model.getOpposeCount() + stepModel.getOpposeStep());
                model.setCommentCount(model.getCommentCount() + stepModel.getCommentStep());
                model.setStarCount(model.getStarCount() + stepModel.getStarStep());
                model.setAnswerCount(model.getAnswerCount() + stepModel.getAnswerStep());
            });
        });
        return infoModels;
    }
}
