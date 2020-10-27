package com.itellyou.service.question.impl;

import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionVersionDetailModel;
import com.itellyou.model.question.QuestionVersionModel;
import com.itellyou.model.question.QuestionVersionTagModel;
import com.itellyou.model.sys.EntityDataModel;
import com.itellyou.model.sys.EntitySearchModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.service.question.QuestionVersionSearchService;
import com.itellyou.service.question.QuestionVersionSingleService;
import com.itellyou.service.question.QuestionVersionTagService;
import com.itellyou.service.sys.EntityService;
import com.itellyou.util.CacheEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = CacheKeys.QUESTION_VERSION_KEY)
@Service
public class QuestionVersionSearchServiceImpl implements QuestionVersionSearchService {

    private final QuestionVersionTagService versionTagService;
    private final QuestionVersionSingleService singleService;
    private final EntityService entityService;

    public QuestionVersionSearchServiceImpl(QuestionVersionTagService versionTagService, QuestionVersionSingleService singleService, EntityService entityService) {
        this.versionTagService = versionTagService;
        this.singleService = singleService;
        this.entityService = entityService;
    }

    @Override
    public List<QuestionVersionDetailModel> searchByQuestionId(Long questionId, Boolean hasContent) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(null,questionId != null ? new HashMap<Long,Integer>(){{ put(questionId,null);}} : null,null,hasContent,null,null,null,null,null,null,order,null,null);
    }

    @Override
    public List<QuestionVersionDetailModel> searchByQuestionId(Long questionId){
        return searchByQuestionId(questionId,false);
    }


    @Override
    @Cacheable(unless = "#result == null")
    public QuestionVersionDetailModel getDetail(Long id) {
        List<QuestionVersionDetailModel> list = search(new HashSet<Long>(){{add(id);}},null,null,true,null,null,null,null,null,null,null,null,null);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public QuestionVersionDetailModel getDetail(Long questionId, Integer version) {
        List<QuestionVersionDetailModel> list = search(null,new HashMap<Long, Integer>(){{put(questionId,version);}},null,true,null,null,null,null,null,null,null,null,null);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<QuestionVersionDetailModel> search(Collection<Long> ids, Map<Long, Integer> questionMap, Long userId, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionVersionModel> versionModels = singleService.search(ids,questionMap,userId,hasContent,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit);
        List<QuestionVersionDetailModel> detailModels = new LinkedList<>();
        if(versionModels.size() == 0) return detailModels;

        Collection<Long> fetchIds = versionModels.stream().map(QuestionVersionModel::getId).collect(Collectors.toSet());
        // 一次查出需要的标签id列表
        Collection<Long> tagIds = new LinkedHashSet<>();
        Map<Long, List<QuestionVersionTagModel>> tagVersionIdList = versionTagService.searchTags(fetchIds);
        for (Map.Entry<Long, List<QuestionVersionTagModel>> mapEntry : tagVersionIdList.entrySet()){
            for (QuestionVersionTagModel versionTagModel : mapEntry.getValue()){
                tagIds.add(versionTagModel.getTagId());
            }
        }

        EntityDataModel<CacheEntity> entityDataModel = entityService.search(versionModels,(QuestionVersionModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.TAG);
            args.put("ids",tagIds);
            return new EntitySearchModel(EntityType.TAG,args);
        },(QuestionVersionModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.USER);
            Collection authorIds = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!authorIds.contains(model.getCreatedUserId())) authorIds.add(model.getCreatedUserId());
            args.put("ids",authorIds);
            return new EntitySearchModel(EntityType.USER,args);
        });

        for (QuestionVersionModel versionModel : versionModels){
            QuestionVersionDetailModel detailModel = new QuestionVersionDetailModel(versionModel);
            detailModel.setAuthor(entityDataModel.get(EntityType.USER,versionModel.getCreatedUserId()));
            List<TagDetailModel> detailTags = new LinkedList<>();
            // 获取标签对应的问题版本
            for (Map.Entry<Long, List<QuestionVersionTagModel>> mapEntry : tagVersionIdList.entrySet()) {
                if(versionModel.getId().equals(mapEntry.getKey())){
                    for (QuestionVersionTagModel versionTagModel : mapEntry.getValue()) {
                        TagDetailModel tagDetailModel = entityDataModel.get(EntityType.TAG,versionTagModel.getTagId());
                        if (tagDetailModel != null) {
                            detailTags.add(tagDetailModel);
                        }
                    }
                }
            }
            detailModel.setTags(detailTags);
            detailModels.add(detailModel);
        }

        return detailModels;
    }
}
