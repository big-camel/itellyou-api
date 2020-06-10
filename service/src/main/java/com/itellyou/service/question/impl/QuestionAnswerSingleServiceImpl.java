package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerDao;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.service.question.QuestionAnswerSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "question_answer")
@Service
public class QuestionAnswerSingleServiceImpl implements QuestionAnswerSingleService {

    private final QuestionAnswerDao infoDao;

    public QuestionAnswerSingleServiceImpl(QuestionAnswerDao infoDao) {
        this.infoDao = infoDao;
    }

    @Override
    public QuestionAnswerModel findByQuestionIdAndUserId(Long questionId, Long userId) {
        return infoDao.findByQuestionIdAndUserId(questionId,userId);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public QuestionAnswerModel findById(Long id) {
        return infoDao.findById(id);
    }

    @Override
    public List<QuestionAnswerModel> search(HashSet<Long> ids, HashSet<Long> questionIds, String mode, Long searchUserId, Long userId,  Integer childCount, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long ip, Integer minComments, Integer maxComments,
                                            Integer minView, Integer maxView,
                                            Integer minSupport, Integer maxSupport,
                                            Integer minOppose, Integer maxOppose,
                                            Integer minStar, Integer maxStar,
                                            Long beginTime, Long endTime,
                                            Map<String, String> order,
                                            Integer offset,
                                            Integer limit) {
        return RedisUtils.fetchByCache("question_answer",QuestionAnswerModel.class,ids,(HashSet<Long> fetchIds) ->
                infoDao.search(fetchIds,questionIds,mode,userId,isAdopted,isDisabled,isPublished,isDeleted,ip,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit)
        );
    }
}
