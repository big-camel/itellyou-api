package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagVersionDao;
import com.itellyou.model.tag.TagVersionModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.tag.TagVersionSearchService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = "tag_version")
@Service
public class TagVersionSearchServiceImpl implements TagVersionSearchService {

    private final TagVersionDao versionDao;
    private final UserSearchService userSearchService;

    public TagVersionSearchServiceImpl(TagVersionDao versionDao, UserSearchService userSearchService) {
        this.versionDao = versionDao;
        this.userSearchService = userSearchService;
    }

    @Override
    public Integer findVersionById(Long id) {
        return versionDao.findVersionById(id);
    }

    @Override
    public List<TagVersionModel> searchByTagId(Long tagId, Boolean hasContent) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return versionDao.search(null,tagId != null ? new HashMap<Long,Integer>(){{put(tagId,null);}} : null,null,hasContent,null,null,null,null,null,null,order,null,null);
    }

    @Override
    public List<TagVersionModel> searchByTagId(Long tagId) {
        return searchByTagId(tagId,false);
    }

    @Override
    public List<TagVersionModel> searchByTagMap(Map<Long, Integer> tagMap, Boolean hasContent) {
        return search(null,tagMap,null,hasContent,null,null,null,null,null,null,null,null,null);
    }

    @Override
    public List<TagVersionModel> search(HashSet<Long> ids, Map<Long, Integer> tagMap, Long userId, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<TagVersionModel> versionModels = RedisUtils.fetchByCache("tag_version", TagVersionModel.class,ids,(HashSet<Long> fetchIds) ->
                versionDao.search(fetchIds,tagMap,userId,true,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit)
        );
        if(versionModels.size() == 0) return versionModels;
        HashSet<Long> authorIds = new LinkedHashSet<>();
        for (TagVersionModel versionModel : versionModels){
            if(hasContent != null && hasContent == false) {
                versionModel.setContent("");
                versionModel.setHtml("");
            }
            if(!authorIds.contains(versionModel.getCreatedUserId())) authorIds.add(versionModel.getCreatedUserId());
        }
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = userSearchService.search(authorIds,null,null,null,null,null,null,null,null,null,null,null);
        for (TagVersionModel versionModel : versionModels){
            // 设置对应的作者
            for (UserDetailModel userDetailModel : userDetailModels){
                if(versionModel.getCreatedUserId().equals(userDetailModel.getId())){
                    versionModel.setAuthor(userDetailModel);
                    break;
                }
            }
        }
        return  versionModels;
    }

    @Override
    public int count(HashSet<Long> ids,Map<Long,Integer> tagMap, Long userId, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip) {
        return versionDao.count(ids,tagMap,userId,isReview,isDisable,isPublish,beginTime,endTime,ip);
    }

    @Override
    public TagVersionModel findById(Long id) {
        return findByTagIdAndId(id,null);
    }

    @Override
    public TagVersionModel findByTagIdAndId(Long id, Long tagId) {
        List<TagVersionModel> list = versionDao.search(id != null ? new HashSet<Long>(){{add(id);}} : null,tagId != null ? new HashMap<Long ,Integer>(){{ put(tagId,null);}} : null,null,true,null,null,null,null,null,null,null,null,null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }
}
