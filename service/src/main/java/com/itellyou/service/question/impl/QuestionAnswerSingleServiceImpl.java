package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerDao;
import com.itellyou.model.common.DataUpdateStepModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerTotalModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.service.common.DataUpdateQueueService;
import com.itellyou.service.question.QuestionAnswerSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = CacheKeys.QUESTION_ANSWER_KEY)
@Service
public class QuestionAnswerSingleServiceImpl implements QuestionAnswerSingleService {

    private final QuestionAnswerDao infoDao;
    private final DataUpdateQueueService updateQueueService;

    public QuestionAnswerSingleServiceImpl(QuestionAnswerDao infoDao, DataUpdateQueueService updateQueueService) {
        this.infoDao = infoDao;
        this.updateQueueService = updateQueueService;
    }

    @Override
    public QuestionAnswerModel findByQuestionIdAndUserId(Long questionId, Long userId,String mode) {
        List<QuestionAnswerModel> answerModels = search(null,new HashSet<Long>(){{ add(questionId);}},mode,userId,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        return answerModels.size() > 0 ? answerModels.get(0) : null;
    }

    @Override
    public QuestionAnswerModel findById(Long id) {
        List<QuestionAnswerModel> answerModels = search(new HashSet<Long>(){{ add(id);}},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        return answerModels.size() > 0 ? answerModels.get(0) : null;
    }

    @Override
    public List<QuestionAnswerModel> search(Collection<Long> ids, Collection<Long> questionIds, String mode, Long userId, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long ip, Integer minComment, Integer maxComment,
                                            Integer minView, Integer maxView,
                                            Integer minSupport, Integer maxSupport,
                                            Integer minOppose, Integer maxOppose,
                                            Integer minStar, Integer maxStar,
                                            Long beginTime, Long endTime,
                                            Map<String, String> order,
                                            Integer offset,
                                            Integer limit) {
        List<QuestionAnswerModel> answerModels = RedisUtils.fetch(CacheKeys.QUESTION_ANSWER_KEY,QuestionAnswerModel.class,ids,(Collection<Long> fetchIds) ->
                infoDao.search(fetchIds,questionIds,mode,userId,isAdopted,isDisabled,isPublished,isDeleted,ip,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit)
        );
        // 从缓存里面计算统计数据值
        List<DataUpdateStepModel> stepModels = updateQueueService.get(EntityType.ANSWER,answerModels.stream().map(QuestionAnswerModel::getId).collect(Collectors.toSet()));
        answerModels.forEach(model -> {
            stepModels.stream().filter(stepModel -> stepModel.getId().equals(model.getId())).findFirst().ifPresent(stepModel -> {
                model.setViewCount(model.getViewCount() + stepModel.getViewStep());
                model.setSupportCount(model.getSupportCount() + stepModel.getSupportStep());
                model.setOpposeCount(model.getOpposeCount() + stepModel.getOpposeStep());
                model.setCommentCount(model.getCommentCount() + stepModel.getCommentStep());
                model.setStarCount(model.getStarCount() + stepModel.getStarStep());
            });
        });
        return answerModels;
    }

    @Override
    public int count(Collection<Long> ids, Collection<Long> questionIds, String mode, Long userId,Boolean isAdopted, Boolean isDisabled, Boolean isPublished,Boolean isDeleted, Long ip, Integer minComment, Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime) {
        return infoDao.count(ids,questionIds,mode,userId,isAdopted,isDisabled,isPublished,isDeleted,ip,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime);
    }

    @Override
    public PageModel<QuestionAnswerModel> page(Collection<Long> ids, Collection<Long> questionIds, String mode, Long userId, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long ip, Integer minComment, Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionAnswerModel> list = search(ids,questionIds,mode,userId,isAdopted,isDisabled,isPublished,isDeleted,ip,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);
        Integer count = count(ids,questionIds,mode,userId,isAdopted,isDisabled,isPublished,isDeleted,ip,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime);
        return new PageModel<>(offset,limit,count,list);
    }

    @Override
    public List<QuestionAnswerTotalModel> totalByUser(Collection<Long> userIds, Long questionId, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return infoDao.totalByUser(userIds,questionId,isAdopted,isDisabled,isPublished,isDeleted,beginTime,endTime,order,offset,limit);
    }
}
