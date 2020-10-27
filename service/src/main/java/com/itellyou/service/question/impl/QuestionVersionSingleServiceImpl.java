package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionVersionDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionVersionModel;
import com.itellyou.service.question.QuestionVersionSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = CacheKeys.QUESTION_VERSION_KEY)
@Service
public class QuestionVersionSingleServiceImpl implements QuestionVersionSingleService {

    private final QuestionVersionDao versionDao;

    public QuestionVersionSingleServiceImpl(QuestionVersionDao versionDao) {
        this.versionDao = versionDao;
    }

    @Override
    public Integer findVersionById(Long id) {
        return versionDao.findVersionById(id);
    }

    @Override
    public List<QuestionVersionModel> searchByQuestionMap(Map<Long, Integer> questionMap, Boolean hasContent) {
        return RedisUtils.fetch(CacheKeys.QUESTION_VERSION_KEY, QuestionVersionModel.class, questionMap, (Map<Long, Integer> fetchMap) ->
                        search(null,questionMap,null,null,hasContent,null,null,null,null,null,null,null,null),
                (key, value) -> key + "-" + value + (hasContent == null || hasContent == true ? "" : "-nc"),
                model -> model.cacheKey() + (hasContent == null || hasContent == true ? "" : "-nc"));
    }

    @Override
    @Cacheable(unless = "#result == null")
    public QuestionVersionModel find(Long id) {
        List<QuestionVersionModel> list = search(id != null ? new HashSet<Long>(){{add(id);}} : null, null,null,true,null,null,null,null,null,null,null,null,null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }

    @Override
    public QuestionVersionModel find(Long questionId, Integer version) {
        List<QuestionVersionModel> list = versionDao.search(null,new HashMap<Long,Integer>(){{ put(questionId,version);}} ,null,true,null,null,null,null,null,null,null,null,null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }

    @Override
    public List<QuestionVersionModel> search(Collection<Long> ids, Map<Long, Integer> questionMap, Long userId, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionVersionModel> versionModels = RedisUtils.fetch(CacheKeys.QUESTION_VERSION_KEY, QuestionVersionModel.class,ids,(Collection<Long> fetchIds) ->
                versionDao.search(fetchIds,questionMap,userId,hasContent,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit),
                id -> id + (hasContent == null || hasContent == true ? "" : "-nc"),
                // 如果没有传ids，那么使用默认cacheKey缓存数据
                model -> (ids != null && ids.size() > 0 ? model.getId() : model.cacheKey()) + (hasContent == null || hasContent == true ? "" : "-nc")
        );
        return  versionModels;
    }

    @Override
    public Integer count(Collection<Long> ids, Map<Long, Integer> questionMap, Long userId, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip) {
        return versionDao.count(ids,questionMap,userId,isReview,isDisable,isPublish,beginTime,endTime,ip);
    }
}
