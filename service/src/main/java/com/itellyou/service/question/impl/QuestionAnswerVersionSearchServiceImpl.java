package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerVersionDao;
import com.itellyou.model.question.QuestionAnswerVersionModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.question.QuestionAnswerVersionSearchService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = "question_answer_version")
@Service
public class QuestionAnswerVersionSearchServiceImpl implements QuestionAnswerVersionSearchService {

    private final QuestionAnswerVersionDao versionDao;
    private final UserSearchService userSearchService;

    public QuestionAnswerVersionSearchServiceImpl(QuestionAnswerVersionDao versionDao, UserSearchService userSearchService) {
        this.versionDao = versionDao;
        this.userSearchService = userSearchService;
    }

    @Override
    public Integer findVersionById(Long id) {
        return versionDao.findVersionById(id);
    }

    @Override
    public QuestionAnswerVersionModel find(Long answerId, Integer version) {
        return versionDao.findByAnswerIdAndVersion(answerId,version);
    }

    @Override
    public List<QuestionAnswerVersionModel> searchByAnswerId(Long answerId, Boolean hasContent) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(null,answerId != null ? new HashMap<Long,Integer>(){{ put(answerId,null);}} : null,null,hasContent,null,null,null,null,null,null,order,null,null);
    }

    @Override
    public List<QuestionAnswerVersionModel> searchByAnswerId(Long answerId) {
        return searchByAnswerId(answerId,false);
    }

    @Override
    public List<QuestionAnswerVersionModel> searchByAnswerMap(Map<Long, Integer> articleMap, Boolean hasContent) {
        return search(null,articleMap,null,hasContent,null,null,null,null,null,null,null,null,null);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public QuestionAnswerVersionModel findById(Long id) {
        return findByAnswerIdAndId(id,null);
    }

    @Override
    public QuestionAnswerVersionModel findByAnswerIdAndId(Long id, Long answerId) {
        List<QuestionAnswerVersionModel> list = search(id != null ? new HashSet<Long>(){{ add(id);}} : null,answerId != null ? new HashMap<Long,Integer>(){{ put(answerId,null);}} : null,null,true,null,null,null,null,null,null,null,null,null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }

    @Override
    public List<QuestionAnswerVersionModel> search(HashSet<Long> ids, Map<Long, Integer> answerMap, Long userId, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionAnswerVersionModel> versionModels = RedisUtils.fetchByCache("question_answer_version", QuestionAnswerVersionModel.class,ids,(HashSet<Long> fetchIds) ->
                versionDao.search(fetchIds,answerMap,userId,hasContent,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit)
        );
        if(versionModels.size() == 0) return versionModels;

        HashSet<Long> authorIds = new LinkedHashSet<>();
        for (QuestionAnswerVersionModel versionModel : versionModels){
            if(!authorIds.contains(versionModel.getCreatedUserId())) authorIds.add(versionModel.getCreatedUserId());
        }
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = authorIds.size() > 0 ? userSearchService.search(authorIds,null,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        for (QuestionAnswerVersionModel versionModel : versionModels) {
            // 设置对应的作者
            for (UserDetailModel userDetailModel : userDetailModels) {
                if (versionModel.getCreatedUserId().equals(userDetailModel.getId())) {
                    versionModel.setAuthor(userDetailModel);
                    break;
                }
            }
        }
        return versionModels;
    }

    @Override
    public Integer count(HashSet<Long> ids, Map<Long, Integer> answerMap, Long userId, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip) {
        return versionDao.count(ids,answerMap,userId,isReview,isDisable,isPublish,beginTime,endTime,ip);
    }
}
