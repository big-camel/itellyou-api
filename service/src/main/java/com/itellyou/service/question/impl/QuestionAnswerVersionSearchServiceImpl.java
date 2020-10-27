package com.itellyou.service.question.impl;

import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionAnswerVersionDetailModel;
import com.itellyou.model.question.QuestionAnswerVersionModel;
import com.itellyou.model.sys.EntityDataModel;
import com.itellyou.model.sys.EntitySearchModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.question.QuestionAnswerVersionSearchService;
import com.itellyou.service.question.QuestionAnswerVersionSingleService;
import com.itellyou.service.sys.EntityService;
import com.itellyou.util.CacheEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@CacheConfig(cacheNames = CacheKeys.QUESTION_ANSWER_VERSION_KEY)
@Service
public class QuestionAnswerVersionSearchServiceImpl implements QuestionAnswerVersionSearchService {

    private final QuestionAnswerVersionSingleService versionSingleService;
    private final EntityService entityService;

    public QuestionAnswerVersionSearchServiceImpl(QuestionAnswerVersionSingleService versionSingleService, EntityService entityService) {
        this.versionSingleService = versionSingleService;
        this.entityService = entityService;
    }

    @Override
    public QuestionAnswerVersionDetailModel getDetail(Long answerId, Integer version) {
        List<QuestionAnswerVersionDetailModel> list = search(null,new HashMap<Long, Integer>(){{put(answerId,version);}},null,null,true,null,null,null,null,null,null,null,null);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<QuestionAnswerVersionDetailModel> searchByAnswerId(Long answerId, Boolean hasContent) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(null,answerId != null ? new HashMap<Long,Integer>(){{ put(answerId,null);}} : null,null,hasContent,null,null,null,null,null,null,order,null,null);
    }

    @Override
    public List<QuestionAnswerVersionDetailModel> searchByAnswerId(Long answerId) {
        return searchByAnswerId(answerId,false);
    }

    @Override
    public QuestionAnswerVersionDetailModel getDetail(Long id) {
        List<QuestionAnswerVersionDetailModel> list = search(new HashSet<Long>(){{add(id);}},null,null,true,null,null,null,null,null,null,null,null,null);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<QuestionAnswerVersionDetailModel> search(Collection<Long> ids, Map<Long, Integer> answerMap, Long userId, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionAnswerVersionModel> versionModels = versionSingleService.search(ids,answerMap,userId,hasContent,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit);
        List<QuestionAnswerVersionDetailModel> detailModels = new LinkedList<>();
        if(versionModels.size() == 0) return detailModels;

        EntityDataModel<CacheEntity> entityDataModel = entityService.search(versionModels,(QuestionAnswerVersionModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.USER);
            Collection authorIds = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!authorIds.contains(model.getCreatedUserId())) authorIds.add(model.getCreatedUserId());
            args.put("ids",authorIds);
            return new EntitySearchModel(EntityType.USER,args);
        });

        for (QuestionAnswerVersionModel versionModel : versionModels){
            QuestionAnswerVersionDetailModel detailModel = new QuestionAnswerVersionDetailModel(versionModel);
            detailModel.setAuthor(entityDataModel.get(EntityType.USER,versionModel.getCreatedUserId()));
        }
        return detailModels;
    }
}
