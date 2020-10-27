package com.itellyou.service.software.impl;

import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.software.SoftwareVersionDetailModel;
import com.itellyou.model.software.SoftwareVersionModel;
import com.itellyou.model.software.SoftwareVersionTagModel;
import com.itellyou.model.sys.EntityDataModel;
import com.itellyou.model.sys.EntitySearchModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.service.software.SoftwareVersionSearchService;
import com.itellyou.service.software.SoftwareVersionSingleService;
import com.itellyou.service.software.SoftwareVersionTagService;
import com.itellyou.service.sys.EntityService;
import com.itellyou.util.CacheEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = CacheKeys.SOFTWARE_VERSION_KEY)
@Service
public class SoftwareVersionSearchServiceImpl implements SoftwareVersionSearchService {

    private final SoftwareVersionTagService versionTagService;
    private final SoftwareVersionSingleService versionSingleService;
    private final EntityService entityService;

    public SoftwareVersionSearchServiceImpl(SoftwareVersionTagService versionTagService, SoftwareVersionSingleService versionSingleService, EntityService entityService) {
        this.versionTagService = versionTagService;
        this.versionSingleService = versionSingleService;
        this.entityService = entityService;
    }

    @Override
    public SoftwareVersionDetailModel getDetail(Long softwareId, Integer version) {
        List<SoftwareVersionDetailModel> list = search(null,new HashMap<Long, Integer>(){{put(softwareId,version);}},null,null,true,null,null,null,null,null,null,null,null,null);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<SoftwareVersionDetailModel> search(Collection<Long> ids, Map<Long, Integer> softwareMap, Long userId, Long groupId, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<SoftwareVersionModel> versionModels = versionSingleService.search(ids,softwareMap,userId,groupId,hasContent,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit);
        List<SoftwareVersionDetailModel> detailModels = new LinkedList<>();
        if(versionModels.size() == 0) return detailModels;
        Collection<Long> fetchIds = versionModels.stream().map(SoftwareVersionModel::getId).collect(Collectors.toSet());
        // 一次查出需要的标签id列表
        Collection<Long> tagIds = new LinkedHashSet<>();
        Map<Long, List<SoftwareVersionTagModel>> tagVersionIdList = versionTagService.searchTags(fetchIds);
        for (Map.Entry<Long, List<SoftwareVersionTagModel>> mapEntry : tagVersionIdList.entrySet()){
            for (SoftwareVersionTagModel versionTagModel : mapEntry.getValue()){
                tagIds.add(versionTagModel.getTagId());
            }
        }

        EntityDataModel<CacheEntity> entityDataModel = entityService.search(versionModels,(SoftwareVersionModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.TAG);
            args.put("ids",tagIds);
            return new EntitySearchModel(EntityType.TAG,args);
        },(SoftwareVersionModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.USER);
            Collection authorIds = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!authorIds.contains(model.getCreatedUserId())) authorIds.add(model.getCreatedUserId());
            args.put("ids",authorIds);
            return new EntitySearchModel(EntityType.USER,args);
        });

        for (SoftwareVersionModel versionModel : versionModels){
            SoftwareVersionDetailModel detailModel = new SoftwareVersionDetailModel(versionModel);
            detailModel.setAuthor(entityDataModel.get(EntityType.USER,versionModel.getCreatedUserId()));
            List<TagDetailModel> detailTags = new LinkedList<>();
            // 获取标签对应的文章
            for (Map.Entry<Long, List<SoftwareVersionTagModel>> mapEntry : tagVersionIdList.entrySet()) {
                if(versionModel.getId().equals(mapEntry.getKey())){
                    for (SoftwareVersionTagModel versionTagModel : mapEntry.getValue()) {
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
        return  detailModels;
    }

    @Override
    public List<SoftwareVersionDetailModel> searchBySoftwareId(Long softwareId, Boolean hasContent) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(null,softwareId != null ? new HashMap<Long,Integer>(){{ put(softwareId,null);}} : null,null,null,hasContent,null,null,null,null,null,null,order,null,null);
    }

    @Override
    public List<SoftwareVersionDetailModel> searchBySoftwareId(Long softwareId){
        return searchBySoftwareId(softwareId,false);
    }

    @Override
    public SoftwareVersionDetailModel getDetail(Long id) {
        List<SoftwareVersionDetailModel> list = search(new HashSet<Long>(){{add(id);}},null,null,null,true,null,null,null,null,null,null,null,null,null);
        return list.size() > 0 ? list.get(0) : null;
    }
}
