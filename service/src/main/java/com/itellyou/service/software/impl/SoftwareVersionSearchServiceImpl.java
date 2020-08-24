package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareVersionDao;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.model.software.SoftwareVersionModel;
import com.itellyou.model.software.SoftwareVersionTagModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.software.SoftwareVersionSearchService;
import com.itellyou.service.software.SoftwareVersionTagService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.RedisUtils;
import com.itellyou.util.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = "software_version")
@Service
public class SoftwareVersionSearchServiceImpl implements SoftwareVersionSearchService {

    private final SoftwareVersionDao versionDao;
    private final UserSearchService userSearchService;
    private final SoftwareVersionTagService versionTagService;
    private final TagSearchService tagSearchService;

    public SoftwareVersionSearchServiceImpl(SoftwareVersionDao versionDao, UserSearchService userSearchService, SoftwareVersionTagService versionTagService, TagSearchService tagSearchService) {
        this.versionDao = versionDao;
        this.userSearchService = userSearchService;
        this.versionTagService = versionTagService;
        this.tagSearchService = tagSearchService;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#softwareId).concat('-').concat(#version)",unless = "#result == null")
    public SoftwareVersionModel find(Long softwareId, Integer version) {
        return versionDao.findBySoftwareIdAndVersion(softwareId,version,true);
    }

    @Override
    public List<SoftwareVersionModel> search(HashSet<Long> ids, Map<Long, Integer> softwareMap, Long userId,Long groupId, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<SoftwareVersionModel> versionModels = RedisUtils.fetchByCache("software_version", SoftwareVersionModel.class,ids,(HashSet<Long> fetchIds) ->
                versionDao.search(fetchIds,softwareMap,userId,groupId,hasContent,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit),
                (SoftwareVersionModel obj, Long id) -> id != null && obj.cacheKey().equals(id.toString()) && (hasContent != null && hasContent == true ? StringUtils.isNotEmpty(obj.getContent()) : true)
        );
        if(versionModels.size() == 0) return versionModels;
        HashSet<Long> authorIds = new LinkedHashSet<>();
        HashSet<Long> fetchIds = new LinkedHashSet<>();
        for (SoftwareVersionModel versionModel : versionModels){
            fetchIds.add(versionModel.getId());
            if(hasContent != null && hasContent == false) {
                versionModel.setContent("");
                versionModel.setHtml("");
            }
            if(!authorIds.contains(versionModel.getCreatedUserId())) authorIds.add(versionModel.getCreatedUserId());
        }
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = authorIds.size() > 0 ? userSearchService.search(authorIds,null,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出需要的标签id列表
        HashSet<Long> tagIds = new LinkedHashSet<>();
        Map<Long, List<SoftwareVersionTagModel>> tagVersionIdList = versionTagService.searchTags(fetchIds);
        for (Map.Entry<Long, List<SoftwareVersionTagModel>> mapEntry : tagVersionIdList.entrySet()){
            for (SoftwareVersionTagModel softwareVersionTagModel : mapEntry.getValue()){
                tagIds.add(softwareVersionTagModel.getTagId());
            }
        }
        // 一次查出需要的标签
        List<TagDetailModel> tagDetailModels = tagIds.size() > 0 ? tagSearchService.search(tagIds,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();

        for (SoftwareVersionModel versionModel : versionModels){
            // 设置对应的作者
            for (UserDetailModel userDetailModel : userDetailModels){
                if(versionModel.getCreatedUserId().equals(userDetailModel.getId())){
                    versionModel.setAuthor(userDetailModel);
                    break;
                }
            }
            List<TagDetailModel> detailTags = new LinkedList<>();
            // 获取标签对应的文章
            for (TagDetailModel tagDetailModel : tagDetailModels) {
                Long versionId = null;
                for (Map.Entry<Long, List<SoftwareVersionTagModel>> mapEntry : tagVersionIdList.entrySet()) {
                    for (SoftwareVersionTagModel versionTagModel : mapEntry.getValue()) {
                        if (versionTagModel.getTagId().equals(tagDetailModel.getId())) {
                            versionId = versionTagModel.getVersionId();
                            break;
                        }
                    }
                }
                if(versionModel.getId().equals(versionId)){
                    detailTags.add(tagDetailModel);
                }
            }
            versionModel.setTags(detailTags);
        }
        return  versionModels;
    }

    @Override
    public Integer count(HashSet<Long> ids, Map<Long, Integer> softwareMap, Long userId,Long groupId,  Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip) {
        return versionDao.count(ids,softwareMap,userId,groupId,isReview,isDisable,isPublish,beginTime,endTime,ip);
    }

    @Override
    public Integer findVersionById(Long id) {
        return versionDao.findVersionById(id);
    }

    @Override
    public List<SoftwareVersionModel> searchBySoftwareId(Long softwareId, Boolean hasContent) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(null,softwareId != null ? new HashMap<Long,Integer>(){{ put(softwareId,null);}} : null,null,null,hasContent,null,null,null,null,null,null,order,null,null);
    }

    @Override
    public List<SoftwareVersionModel> searchBySoftwareId(Long softwareId){
        return searchBySoftwareId(softwareId,false);
    }

    @Override
    public List<SoftwareVersionModel> searchBySoftwareMap(Map<Long, Integer> softwareMap, Boolean hasContent) {
        return search(null,softwareMap,null,null,hasContent,null,null,null,null,null,null,null,null,null);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public SoftwareVersionModel findById(Long id) {
        return findBySoftwareIdAndId(id,null);
    }

    @Override
    public SoftwareVersionModel findBySoftwareIdAndId(Long id, Long softwareId) {
        List<SoftwareVersionModel> list = search(id != null ? new HashSet<Long>(){{add(id);}} : null, softwareId != null ? new HashMap<Long,Integer>() {{ put(softwareId,null);}} : null, null,null, true, null, null, null, null, null, null, null, null, null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }
}
