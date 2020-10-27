package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagInfoDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.*;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.sys.EntitySearchService;
import com.itellyou.service.tag.*;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.Params;
import com.itellyou.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = CacheKeys.TAG_KEY)
@Service
public class TagSearchServiceImpl implements TagSearchService , EntitySearchService<TagDetailModel> {

    private final TagInfoDao tagInfoDao;
    private final UserSearchService userSearchService;
    private final TagStarSingleService starSingleService;
    private final TagGroupSingleService groupSingleService;
    private final TagSingleService tagSingleService;
    private final TagVersionSingleService versionSingleService;

    @Autowired
    public TagSearchServiceImpl(TagInfoDao tagInfoDao,UserSearchService userSearchService, TagStarSingleService starSingleService, TagGroupSingleService groupSingleService, TagSingleService tagSingleService, TagVersionSingleService versionSingleService){
        this.tagInfoDao = tagInfoDao;
        this.userSearchService = userSearchService;
        this.groupSingleService = groupSingleService;
        this.starSingleService = starSingleService;
        this.tagSingleService = tagSingleService;
        this.versionSingleService = versionSingleService;
    }

    @Override
    public List<TagDetailModel> search(Collection<Long> ids, String name, String mode, Collection<Long> groupIds, Long userId,
                                       Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isPublished, Long ip,
                                       Integer minStar, Integer maxStar,
                                       Integer minQuestion, Integer maxQuestion,
                                       Integer minArticle, Integer maxArticle,
                                       Long beginTime, Long endTime,
                                       Map<String,String> order,
                                       Integer offset,
                                       Integer limit) {
         List<TagInfoModel> infoModels = tagSingleService.search(ids,name,mode,groupIds,userId,
                 isDisabled,isPublished,ip,minStar,maxStar,minQuestion,maxQuestion,minArticle,maxArticle,beginTime,endTime,order,offset,limit);

        List<TagDetailModel> detailModels = new LinkedList<>();
        Collection<Long> authorIds = new LinkedHashSet<>();
        Collection<Long> fetchGroupIds = new LinkedHashSet<>();
        Collection<Long> fetchIds = new LinkedHashSet<>();
        HashMap<Long,Integer> versionMap = new LinkedHashMap<>();
        List<TagVersionModel> versionModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;
        for (TagInfoModel infoModel : infoModels){
            TagDetailModel detailModel = new TagDetailModel(infoModel);

            // 获取内容
            if(hasContent == null || hasContent == true  || mode == "draft"){
                versionMap.put(infoModel.getId(),!"draft".equals(mode) ? infoModel.getVersion() : infoModel.getDraft());
            }
            fetchIds.add(infoModel.getId());
            // 获取作者
            if(!authorIds.contains(infoModel.getCreatedUserId())) authorIds.add(infoModel.getCreatedUserId());
            // 获取分组
            if(infoModel.getGroupId() != null && !fetchGroupIds.contains(infoModel.getGroupId())) fetchGroupIds.add(infoModel.getGroupId());
            detailModels.add(detailModel);
        }
        // 一次查出需要的版本信息
        versionModels = versionMap.size() > 0 ? versionSingleService.searchByTagMap(versionMap,hasContent) : new ArrayList<>();
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = userSearchService.search(authorIds,searchUserId,null,null,null,null,null,null,null,null,null,null);
        // 一次查出需要的分组
        List<TagGroupModel> groupModels = new ArrayList<>();
        if(fetchGroupIds.size() > 0){
            groupModels = groupSingleService.search(fetchGroupIds,null,null,null,null,null,null,null,null,null,null,null);
        }
        // 一次查出是否关注
        List<TagStarModel> starModels = new ArrayList<>();
        if(searchUserId != null){
            starModels = starSingleService.search(fetchIds,searchUserId);
        }
        for (TagDetailModel detailModel : detailModels){
            // 设置版本信息
            for(TagVersionModel versionModel : versionModels){
                if(versionModel.getTagId().equals(detailModel.getId())) {
                    detailModel.setHtml(versionModel.getHtml());
                    detailModel.setContent(versionModel.getContent());

                    if (mode == "draft") {
                        detailModel.setDescription(versionModel.getDescription());
                        detailModel.setUpdatedIp(versionModel.getCreatedIp());
                        detailModel.setUpdatedTime(versionModel.getCreatedTime());
                        detailModel.setUpdatedUserId(versionModel.getCreatedUserId());
                    }
                    break;
                }
            }
            // 设置对应的作者
            for (UserDetailModel userDetailModel : userDetailModels){
                if(detailModel.getCreatedUserId().equals(userDetailModel.getId())){
                    detailModel.setAuthor(userDetailModel);
                    break;
                }
            }
            // 设置对应的分组
            for (TagGroupModel groupModel : groupModels){
                if(groupModel.getId().equals(detailModel.getGroupId())){
                    detailModel.setGroup(groupModel);
                    break;
                }
            }
            // 设置是否关注
            for (TagStarModel starModel : starModels){
                if(starModel.getTagId().equals(detailModel.getId())){
                    detailModel.setUseStar(true);
                    break;
                }
            }
        }
        return detailModels;
    }

    @Override
    public int count(Collection<Long> ids, String name, String mode, Collection<Long> groupIds, Long userId, Boolean isDisabled, Boolean isPublished, Long ip, Integer minStar, Integer maxStar, Integer minQuestion, Integer maxQuestion, Integer minArticle, Integer maxArticle, Long beginTime, Long endTime) {
        return tagInfoDao.count(ids,name,mode,groupIds,userId,
                isDisabled,isPublished,ip,minStar,maxStar,minQuestion,maxQuestion,minArticle,maxArticle,beginTime,endTime);
    }

    @Override
    public List<TagDetailModel> search(String name,String mode, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,name,mode,null,null,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<TagDetailModel> search(String name, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(name,null,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<TagDetailModel> search(String name,String mode, Integer offset, Integer limit) {
        return search(name,mode,null,null,null,offset,limit);
    }

    @Override
    public List<TagDetailModel> search(String name, Integer offset, Integer limit) {
        return search(name,null,offset,limit);
    }

    @Override
    public PageModel<TagDetailModel> page(String name, String mode, Long groupId, Long userId, Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isPublished, Long ip, Integer minStar, Integer maxStar, Integer minQuestion, Integer maxQuestion, Integer minArticle, Integer maxArticle, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<TagDetailModel> list = search(null,name,mode,groupId != null ? new HashSet<Long>(){{add(groupId);}} : null,userId,searchUserId,hasContent,isDisabled,isPublished,ip,minStar,maxStar,minQuestion,maxQuestion,minArticle,maxArticle,beginTime,endTime,order,offset,limit);
        Integer total = count(null,name,mode,groupId != null ? new HashSet<Long>(){{add(groupId);}} : null,userId,isDisabled,isPublished,ip,minStar,maxStar,minQuestion,maxQuestion,minArticle,maxArticle,beginTime,endTime);
        return new PageModel<>(offset,limit,total,list);
    }

    @Override
    public List<TagInfoModel> searchChild(Collection<Long> ids, String name, String mode, Collection<Long> groupIds, Integer childCount, Long userId, Boolean isDisabled, Boolean isPublished, Long ip, Integer minStar, Integer maxStar, Integer minQuestion, Integer maxQuestion, Integer minArticle, Integer maxArticle, Long beginTime, Long endTime, Map<String, String> order) {
        return RedisUtils.fetch(CacheKeys.TAG_KEY, TagInfoModel.class,ids,(Collection<Long> fetchIds) ->
                tagInfoDao.searchChild(fetchIds,name,mode,groupIds,childCount,userId,isDisabled,isPublished,ip,minStar,maxStar,minQuestion,maxQuestion,minArticle,maxArticle,beginTime,endTime,order)
        );
    }

    @Override
    public TagDetailModel getDetail(Long id, Long userId,String mode, Long searchUserId, Boolean hasContent) {
        List<TagDetailModel> listTag = search(id != null ? new HashSet<Long>(){{add(id);}} : null,null,mode,null,userId,searchUserId,hasContent,null,null,null,null,null,null,null,null,null,null,null,null,0,1);
        return listTag != null && listTag.size() > 0 ? listTag.get(0) : null;
    }

    @Override
    public TagDetailModel getDetail(Long id,String mode, Long userId) {
        return getDetail(id,userId,mode,null,null);
    }

    @Override
    public TagDetailModel getDetail(Long id) {
        return getDetail(id,null,null);
    }

    @Override
    public List<TagDetailModel> search(Map<String, Object> args) {
        Params params = new Params(args);
        return search(params.get("ids",Collection.class),
                params.get("name",String.class),
                params.get("mode",String.class),
                params.get("groupIds",Collection.class),
                params.get("userId",Long.class),
                params.get("searchUserId",Long.class),
                params.get("hasContent",Boolean.class),
                params.get("isDisabled",Boolean.class),
                params.get("isPublished",Boolean.class),
                params.get("ip",Long.class),
                params.get("minStar",Integer.class),
                params.get("maxStar",Integer.class),
                params.get("minQuestion",Integer.class),
                params.get("maxQuestion",Integer.class),
                params.get("minArticle",Integer.class),
                params.get("maxArticle",Integer.class),
                params.get("beginTime",Long.class),
                params.get("endTime",Long.class),
                params.get("order",Map.class),
                params.get("offset",Integer.class),
                params.get("limit",Integer.class));
    }
}
