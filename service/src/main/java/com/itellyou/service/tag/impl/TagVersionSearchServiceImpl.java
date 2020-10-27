package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagVersionDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.EntityDataModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.tag.TagVersionDetailModel;
import com.itellyou.model.tag.TagVersionModel;
import com.itellyou.service.sys.EntityService;
import com.itellyou.service.tag.TagVersionSearchService;
import com.itellyou.service.tag.TagVersionSingleService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.CacheEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = CacheKeys.TAG_VERSION_KEY)
@Service
public class TagVersionSearchServiceImpl implements TagVersionSearchService {

    private final TagVersionDao versionDao;
    private final UserSearchService userSearchService;
    private final TagVersionSingleService singleService;
    private final EntityService entityService;

    public TagVersionSearchServiceImpl(TagVersionDao versionDao, UserSearchService userSearchService, TagVersionSingleService singleService, EntityService entityService) {
        this.versionDao = versionDao;
        this.userSearchService = userSearchService;
        this.singleService = singleService;
        this.entityService = entityService;
    }

    @Override
    public List<TagVersionDetailModel> searchByTagId(Long tagId, Boolean hasContent) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(null,tagId != null ? new HashMap<Long,Integer>(){{ put(tagId,null);}} : null,null,hasContent,null,null,null,null,null,null,order,null,null);
    }

    @Override
    public List<TagVersionDetailModel> searchByTagId(Long tagId) {
        return searchByTagId(tagId,false);
    }

    @Override
    public List<TagVersionDetailModel> search(Collection<Long> ids, Map<Long, Integer> tagMap, Long userId, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<TagVersionModel> versionModels = singleService.search(ids,tagMap,userId,hasContent,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit);
        List<TagVersionDetailModel> detailModels = new LinkedList<>();
        if(versionModels.size() == 0) return detailModels;

        EntityDataModel<CacheEntity> entityDataModel = entityService.search(EntityType.USER,"ids",versionModels.stream().map(TagVersionModel::getCreatedUserId).collect(Collectors.toSet()));
        for (TagVersionModel versionModel : versionModels){
            TagVersionDetailModel detailModel = new TagVersionDetailModel(versionModel);
            detailModel.setAuthor(entityDataModel.get(EntityType.USER,versionModel.getCreatedUserId()));
            detailModels.add(detailModel);
        }
        return  detailModels;
    }

    @Override
    public TagVersionDetailModel getDetail(Long id) {
        List<TagVersionDetailModel> list = search(new HashSet<Long>(){{add(id);}},null,null,true,null,null,null,null,null,null,null,null,null);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public TagVersionDetailModel getDetail(Long tagId, Integer version) {
        List<TagVersionDetailModel> list = search(null,new HashMap<Long, Integer>(){{put(tagId,version);}},null,true,null,null,null,null,null,null,null,null,null);
        return list.size() > 0 ? list.get(0) : null;
    }
}
