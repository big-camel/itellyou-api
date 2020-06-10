package com.itellyou.service.tag.impl;

import com.itellyou.dao.tag.TagGroupDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagGroupDetailModel;
import com.itellyou.model.tag.TagGroupModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.tag.TagGroupSearchService;
import com.itellyou.service.tag.TagGroupSingleService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.user.UserSearchService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TagGroupSearchServiceImpl implements TagGroupSearchService {

    private final TagGroupDao groupDao;
    private final TagGroupSingleService singleService;
    private final UserSearchService userSearchService;
    private final TagSearchService tagSearchService;

    public TagGroupSearchServiceImpl(TagGroupDao groupDao, TagGroupSingleService singleService, UserSearchService userSearchService, TagSearchService tagSearchService) {
        this.groupDao = groupDao;
        this.singleService = singleService;
        this.userSearchService = userSearchService;
        this.tagSearchService = tagSearchService;
    }

    @Override
    public List<TagGroupDetailModel> search(HashSet<Long> ids, Long userId, Integer childCount, Long ip, Boolean isDisabled, Boolean isPublished, Integer minTagCount, Integer maxTagCount, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<TagGroupModel> groupModels = singleService.search(ids,userId,ip,isDisabled,isPublished,minTagCount,maxTagCount,beginTime,endTime,order,offset,limit);
        List<TagGroupDetailModel> detailModels = new LinkedList<>();

        HashSet<Long> authorIds = new LinkedHashSet<>();
        HashSet<Long> fetchIds = new LinkedHashSet<>();
        for (TagGroupModel infoModel : groupModels){
            TagGroupDetailModel detailModel = new TagGroupDetailModel(infoModel);

            fetchIds.add(infoModel.getId());
            // 获取作者
            if(infoModel.getCreatedUserId() != null && !authorIds.contains(infoModel.getCreatedUserId()))
                authorIds.add(infoModel.getCreatedUserId());
            detailModels.add(detailModel);
        }
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = userSearchService.search(authorIds,null,null,null,null,null,null,null,null,null,null,null);
        // 一次查出需要的标签
        List<TagDetailModel> tagDetailModels = new LinkedList<>();
        if(childCount > 0){
            List<TagInfoModel> tagInfoModels = tagSearchService.searchChild(null,null,null,fetchIds,childCount,null,isDisabled,isPublished,null,null,null,null,null,null,null,null,null,null);
            HashSet<Long> tagIds = new LinkedHashSet<>();
            for (TagInfoModel tagInfoModel : tagInfoModels){
                tagIds.add(tagInfoModel.getId());
            }
            tagDetailModels = tagSearchService.search(tagIds,null,null,null,null,null,null,null,null,null,null,null,null,null
            ,null,null,null,null,null,null,null);
        }
        for(TagGroupDetailModel detailModel : detailModels){
            // 设置对应的作者
            for (UserDetailModel userDetailModel : userDetailModels){
                if(detailModel.getCreatedUserId().equals(userDetailModel.getId())){
                    detailModel.setAuthor(userDetailModel);
                    break;
                }
            }
            List<TagDetailModel> detailTags = new LinkedList<>();
            // 获取标签对应的分组
            for (TagDetailModel tagDetailModel : tagDetailModels){
                if(detailModel.getId().equals(tagDetailModel.getGroupId())){
                    detailTags.add(tagDetailModel);
                }
            }
            detailModel.setTagList(detailTags);
        }
        return detailModels;
    }

    @Override
    public PageModel<TagGroupDetailModel> page(HashSet<Long> ids,
                                               Long userId,
                                               Integer childCount,
                                               Long ip,
                                               Boolean isDisabled, Boolean isPublished,
                                               Integer minTagCount, Integer maxTagCount,
                                               Long beginTime, Long endTime,
                                               Map<String,String> order,
                                               Integer offset,
                                               Integer limit) {
        if(offset == null) offset=0;
        if(limit == null) limit = 10;
        List<TagGroupDetailModel> data = search(ids,userId,childCount,ip,isDisabled,isPublished,minTagCount,maxTagCount,beginTime,endTime,order,offset,limit);
        Integer total = count(null,userId,ip,null,null,minTagCount,maxTagCount,beginTime,endTime);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public int count(HashSet<Long> ids, Long userId, Long ip,Boolean isDisabled,Boolean isPublished, Integer minTagCount, Integer maxTagCount, Long beginTime, Long endTime) {
        return groupDao.count(ids,userId,ip,isDisabled,isPublished,minTagCount,maxTagCount,beginTime,endTime);
    }
}
