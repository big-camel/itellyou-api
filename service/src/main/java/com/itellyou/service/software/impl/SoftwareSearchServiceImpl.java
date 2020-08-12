package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareInfoDao;
import com.itellyou.model.software.*;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.software.*;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = "software")
@Service
public class SoftwareSearchServiceImpl implements SoftwareSearchService {

    private final SoftwareInfoDao softwareInfoDao;
    private final SoftwareVersionSearchService versionSearchService;
    private final SoftwareTagService softwareTagService;
    private final TagSearchService tagSearchService;
    private final SoftwareVersionTagService versionTagService;
    private final SoftwareGroupService groupService;
    private final UserSearchService userSearchService;
    private final SoftwareVoteService softwareVoteService;
    private final SoftwareAttributesService attributesService;
    private final SoftwareReleaseService releaseService;

    @Autowired
    public SoftwareSearchServiceImpl(SoftwareInfoDao softwareInfoDao, SoftwareVersionSearchService versionSearchService, SoftwareTagService softwareTagService, TagSearchService tagSearchService, SoftwareVersionTagService versionTagService, SoftwareGroupService groupService, UserSearchService userSearchService, SoftwareVoteService softwareVoteService, SoftwareAttributesService attributesService, SoftwareReleaseService releaseService){
        this.softwareInfoDao = softwareInfoDao;
        this.versionSearchService = versionSearchService;
        this.softwareTagService = softwareTagService;
        this.tagSearchService = tagSearchService;
        this.versionTagService = versionTagService;
        this.groupService = groupService;
        this.userSearchService = userSearchService;
        this.softwareVoteService = softwareVoteService;
        this.attributesService = attributesService;
        this.releaseService = releaseService;
    }

    private HashSet<Long> formTags(HashSet<Long> tags){
        if(tags != null && tags.size() > 0){
            return softwareTagService.searchSoftwareId(tags);
        }
        return new HashSet<>();
    }

    @Override
    public List<SoftwareDetailModel> search(HashSet<Long> ids, String mode, Long groupId, Long userId, Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
                                           HashSet<Long> tags, Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose,Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {

        if(ids == null) ids = new HashSet<>();
        ids.addAll(formTags(tags));
        if(tags != null && ids.size() == 0){
            return new LinkedList<>();
        }
        List<SoftwareInfoModel> infoModels = RedisUtils.fetchByCache("software",SoftwareInfoModel.class,ids,(HashSet<Long> fetchIds) ->
                        softwareInfoDao.search(fetchIds,mode,groupId,userId,isDisabled,isPublished,isDeleted, minComments, maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip,order,offset,limit)
                );
        List<SoftwareDetailModel> detailModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;
        HashSet<Long> groupIds = new LinkedHashSet<>();
        HashSet<Long> authorIds = new LinkedHashSet<>();
        HashSet<Long> fetchIds = new LinkedHashSet<>();
        HashMap<Long,Integer> versionMap = new LinkedHashMap<>();
        List<SoftwareVersionModel> versionModels = new LinkedList<>();
        for (SoftwareInfoModel infoModel : infoModels){
            SoftwareDetailModel detailModel = new SoftwareDetailModel(infoModel);

            // 获取内容
            if(hasContent == null || hasContent == true  || mode == "draft"){
                versionMap.put(infoModel.getId(),!"draft".equals(mode) ? infoModel.getVersion() : infoModel.getDraft());
            }
            fetchIds.add(infoModel.getId());
            // 获取分组
            if(infoModel.getGroupId() != null && infoModel.getGroupId() > 0 && !groupIds.contains(infoModel.getGroupId())){
                groupIds.add(infoModel.getGroupId());
            }
            // 获取作者
            if(!authorIds.contains(infoModel.getCreatedUserId())) authorIds.add(infoModel.getCreatedUserId());

            if(searchUserId != null){
                boolean isEquals = infoModel.getCreatedUserId().equals(searchUserId);

                detailModel.setAllowEdit(isEquals);
                detailModel.setAllowOppose(!isEquals);
                detailModel.setAllowSupport(!isEquals);
            }

            detailModels.add(detailModel);
        }
        // 一次查出需要的版本信息
        HashSet<Long> versionIds = new LinkedHashSet<>();
        versionModels = versionMap.size() > 0 ? versionSearchService.searchBySoftwareMap(versionMap,hasContent) : new ArrayList<>();
        for (SoftwareVersionModel versionModel : versionModels){
            versionIds.add(versionModel.getId());
        }
        // 一次查出需要的分组
        List<SoftwareGroupModel> groupModels = groupIds.size() > 0 ? groupService.search(groupIds,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = authorIds.size() > 0 ? userSearchService.search(authorIds,searchUserId,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出需要的标签id列表
        HashSet<Long> tagIds = new LinkedHashSet<>();
        Map<Long, List<SoftwareVersionTagModel>> tagVersionIdList = new HashMap<>();
        Map<Long, List<SoftwareTagModel>> tagSoftwareIdList = new HashMap<>();
        if("draft".equals(mode) && versionIds.size() > 0){
            tagVersionIdList = versionTagService.searchTags(versionIds);
            for (Map.Entry<Long, List<SoftwareVersionTagModel>> mapEntry : tagVersionIdList.entrySet()){
                for (SoftwareVersionTagModel softwareVersionTagModel : mapEntry.getValue()){
                    tagIds.add(softwareVersionTagModel.getTagId());
                }
            }
        }
        else if(fetchIds.size() > 0){
            tagSoftwareIdList = softwareTagService.searchTags(fetchIds);
            for (Map.Entry<Long, List<SoftwareTagModel>> mapEntry : tagSoftwareIdList.entrySet()){
                for (SoftwareTagModel softwareTagModel : mapEntry.getValue()){
                    tagIds.add(softwareTagModel.getTagId());
                }
            }
        }
        // 一次查出需要的标签
        List<TagDetailModel> tagDetailModels = tagIds.size() > 0 ? tagSearchService.search(tagIds,null,null,null,null,searchUserId,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出是否有关注,是否点赞
        List<SoftwareVoteModel> voteModels = new ArrayList<>();
        if(searchUserId != null){
            voteModels = softwareVoteService.search(fetchIds,searchUserId);
        }

        for (SoftwareDetailModel detailModel : detailModels){
            // 设置版本信息
            for(SoftwareVersionModel versionModel : versionModels){
                if(versionModel.getSoftwareId().equals(detailModel.getId())) {
                    detailModel.setHtml(versionModel.getHtml());
                    detailModel.setContent(versionModel.getContent());
                    if (mode == "draft") {
                        detailModel.setName(versionModel.getName());
                        detailModel.setDescription(versionModel.getDescription());
                        detailModel.setGroupId(versionModel.getGroupId());
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
            List<TagDetailModel> detailTags = new LinkedList<>();
            // 获取标签对应的文章
            for (TagDetailModel tagDetailModel : tagDetailModels){
                if("draft".equals(mode)) {
                    for (Map.Entry<Long, List<SoftwareVersionTagModel>> mapEntry : tagVersionIdList.entrySet()) {
                        for (SoftwareVersionTagModel softwareVersionTagModel : mapEntry.getValue()) {
                            if (softwareVersionTagModel.getTagId().equals(tagDetailModel.getId())) {
                                Long versionId = softwareVersionTagModel.getVersionId();
                                for(SoftwareVersionModel versionModel : versionModels){
                                    if(versionId.equals(versionModel.getId())){
                                        if(detailModel.getId().equals(versionModel.getSoftwareId())){
                                            detailTags.add(tagDetailModel);
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }else{
                    for (Map.Entry<Long, List<SoftwareTagModel>> mapEntry : tagSoftwareIdList.entrySet()) {
                        for (SoftwareTagModel softwareTagModel : mapEntry.getValue()) {
                            if (softwareTagModel.getTagId().equals(tagDetailModel.getId())) {
                                if(detailModel.getId().equals(softwareTagModel.getSoftwareId())){
                                    detailTags.add(tagDetailModel);
                                }
                            }
                        }
                    }
                }
            }
            detailModel.setTags(detailTags);
            // 设置分组
            for (SoftwareGroupModel groupModel : groupModels){
                if(groupModel.getId().equals(detailModel.getGroupId())){
                    detailModel.setGroup(groupModel);
                    break;
                }
            }
            // 获取是否点赞
            for(SoftwareVoteModel voteModel : voteModels){
                if(voteModel.getSoftwareId().equals(detailModel.getId())){
                    detailModel.setUseOppose(VoteType.OPPOSE.equals(voteModel.getType()));
                    detailModel.setUseSupport(VoteType.SUPPORT.equals(voteModel.getType()));
                }
            }
            // 一次查出属性
            List<SoftwareAttributesModel> attributesModels = attributesService.search(fetchIds);
            for (SoftwareAttributesModel attributesModel : attributesModels){
                if(attributesModel.getSoftwareId().equals(detailModel.getId())){
                    detailModel.getAttributes().add(attributesModel);
                }
            }
            // 一次查出版本
            List<SoftwareReleaseDetailModel> releaseDetailModels = releaseService.search(fetchIds);
            for (SoftwareReleaseDetailModel releaseDetailModel : releaseDetailModels){
                if(releaseDetailModel.getSoftwareId().equals(detailModel.getId())){
                    detailModel.getReleases().add(releaseDetailModel);
                }
            }
        }
        return detailModels;
    }

    @Override
    public int count(HashSet<Long> ids, String mode,Long groupId, Long userId, Boolean isDisabled,Boolean isDeleted, Boolean isPublished,HashSet<Long> tags,Integer minComments,Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip) {
        if(ids == null) ids = new HashSet<>();
        ids.addAll(formTags(tags));
        if(tags != null && ids.size() == 0){
            return 0;
        }
        return softwareInfoDao.count(ids,mode,groupId,userId,isDisabled,isPublished,isDeleted,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip);
    }

    @Override
    public List<SoftwareDetailModel> search(HashSet<Long> ids, String mode,Long groupId, Long userId,Long searchUserId,Boolean hasContent, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(ids,mode,groupId,userId,searchUserId,hasContent,null,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public List<SoftwareDetailModel> search(Long groupId,Long searchUserId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished,
                                           HashSet<Long> tags,Integer minComments,Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,null,groupId,null,searchUserId,hasContent,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public List<SoftwareDetailModel> search(Long groupId,Long searchUserId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished,HashSet<Long> tags,Integer minComments,Integer maxComments, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(groupId,searchUserId,hasContent,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Long groupId, Boolean isDisabled,Boolean isDeleted, Boolean isPublished,HashSet<Long> tags,Integer minComments,Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime) {
        return count(null,null,groupId,null,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null);
    }

    @Override
    public List<SoftwareDetailModel> search(Long groupId,Long searchUserId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(groupId,searchUserId,hasContent,isDisabled,isDeleted,isPublished,null,null,null,null,null,null,null,null,null,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<SoftwareDetailModel> search(Long groupId,Long searchUserId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(groupId,searchUserId,hasContent,isDisabled,isDeleted,isPublished,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Long groupId, Boolean isDisabled,Boolean isDeleted, Boolean isPublished, Long beginTime, Long endTime) {
        return count(groupId,isDisabled,isDeleted,isPublished,null,null,null,null,null,null,null,null,null,beginTime,endTime);
    }

    @Override
    public PageModel<SoftwareDetailModel> page(HashSet<Long> ids, String mode, Long groupId, Long userId, Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished, HashSet<Long> tags, Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<SoftwareDetailModel> data = search(ids,mode,groupId,userId,searchUserId,hasContent,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(ids,mode,groupId,userId,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public PageModel<SoftwareDetailModel> page(Long groupId,Long searchUserId,Boolean hasContent,Boolean isDisabled, Boolean isDeleted, Boolean isPublished, HashSet<Long> tags,Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Long beginTime, Long endTime, Map<String, String> order,Integer offset, Integer limit) {
        return page(null,null,groupId,null,searchUserId,hasContent,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public PageModel<SoftwareDetailModel> page(Long groupId,Long searchUserId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished, Long beginTime, Long endTime, Integer offset, Integer limit) {
        return page(groupId,searchUserId,hasContent,isDisabled,isDeleted,isPublished,null,null,null,null,null,null,null,null,null,beginTime,endTime,null,offset,limit);
    }

    @Override
    public SoftwareDetailModel getDetail(Long id, String mode, Long userId) {
        List<SoftwareDetailModel> listSoftware = search(id != null ? new HashSet<Long>(){{add(id);}} : null,mode,null,userId,null,null,null,null,null,0,1);
        return listSoftware != null && listSoftware.size() > 0 ? listSoftware.get(0) : null;
    }

    @Override
    public SoftwareDetailModel getDetail(Long id, String mode) {
        return getDetail(id,mode,null);
    }

    @Override
    public SoftwareDetailModel getDetail(Long id, Long userId, Long searchUserId) {
        List<SoftwareDetailModel> listSoftware = search(id != null ? new HashSet<Long>(){{add(id);}} : null,null,null,userId,searchUserId,null,null,null,null,0,1);
        return listSoftware != null && listSoftware.size() > 0 ? listSoftware.get(0) : null;
    }

    @Override
    public SoftwareDetailModel getDetail(Long id) {
        return getDetail(id,(String) null);
    }

    @Override
    public SoftwareDetailModel getDetail(Long id, Long userId) {
        return getDetail(id,(String) null,userId);
    }
}
