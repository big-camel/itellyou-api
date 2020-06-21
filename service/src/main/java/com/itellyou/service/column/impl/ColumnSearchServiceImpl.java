package com.itellyou.service.column.impl;

import com.itellyou.dao.column.ColumnInfoDao;
import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.column.ColumnStarModel;
import com.itellyou.model.column.ColumnTagModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysPath;
import com.itellyou.model.sys.SysPathModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.column.ColumnSearchService;
import com.itellyou.service.column.ColumnStarSingleService;
import com.itellyou.service.column.ColumnTagService;
import com.itellyou.service.sys.SysPathService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = "column")
@Service
public class ColumnSearchServiceImpl implements ColumnSearchService {

    private final ColumnInfoDao columnInfoDao;
    private final SysPathService pathService;
    private final ColumnTagService columnTagService;
    private final TagSearchService tagSearchService;
    private final UserSearchService userSearchService;
    private final ColumnStarSingleService starSingleService;

    public ColumnSearchServiceImpl(ColumnInfoDao columnInfoDao, SysPathService pathService, ColumnTagService columnTagService, TagSearchService tagSearchService, UserSearchService userSearchService, ColumnStarSingleService starSingleService){
        this.columnInfoDao = columnInfoDao;
        this.pathService = pathService;
        this.columnTagService = columnTagService;
        this.tagSearchService = tagSearchService;
        this.userSearchService = userSearchService;
        this.starSingleService = starSingleService;
    }

    private HashSet<Long> formTags(HashSet<Long> tags){
        if(tags != null && tags.size() > 0){
            return columnTagService.searchColumnId(tags);
        }
        return new HashSet<>();
    }

    @Override
    public List<ColumnDetailModel> search(HashSet<Long> ids, String name, Long userId,Long memberId, Long searchUserId, Boolean isDisabled, Boolean isReviewed, Boolean isDeleted, HashSet<Long> tags, Integer minArticles, Integer maxArticles, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(ids == null) ids = new HashSet<>();
        ids.addAll(formTags(tags));
        if(tags != null && ids.size() == 0){
            return new LinkedList<>();
        }
        List<ColumnInfoModel> infoModels = RedisUtils.fetchByCache("column",ColumnInfoModel.class,ids,(HashSet<Long> fetchIds) ->
                columnInfoDao.search(fetchIds,name,userId,memberId,searchUserId,isDisabled,isReviewed,isDeleted,minArticles,maxArticles,minStars,maxStars,beginTime,endTime,ip,order,offset,limit)
        );
        List<ColumnDetailModel> detailModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;
        HashSet<Long> authorIds = new LinkedHashSet<>();
        HashSet<Long> fetchIds = new LinkedHashSet<>();
        // 转换为详细model
        for (ColumnInfoModel infoModel : infoModels) {
            ColumnDetailModel detailModel = new ColumnDetailModel(infoModel);
            // 添加作者id
            if(!authorIds.contains(infoModel.getCreatedUserId())) authorIds.add(infoModel.getCreatedUserId());
            // 添加需要查询id
            fetchIds.add(infoModel.getId());
            detailModels.add(detailModel);
        }
        // 一次获取路径信息
        List<SysPathModel> pathModels = fetchIds.size() > 0 ? pathService.search(SysPath.COLUMN,fetchIds) : new ArrayList<>();
        // 一次查出需要的标签id列表
        HashSet<Long> tagIds = new LinkedHashSet<>();
        Map<Long, List<ColumnTagModel>> tagIdList = columnTagService.searchTags(fetchIds);
        for (Map.Entry<Long, List<ColumnTagModel>> mapEntry : tagIdList.entrySet()){
            for (ColumnTagModel columnTagModel : mapEntry.getValue()){
                tagIds.add(columnTagModel.getTagId());
            }
        }
        // 一次查出需要的标签
        List<TagDetailModel> tagDetailModels = tagIds.size() > 0 ? tagSearchService.search(tagIds,null,null,null,null,searchUserId,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = authorIds.size() > 0 ? userSearchService.search(authorIds,searchUserId,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出是否有关注
        List<ColumnStarModel> starModels = new ArrayList<>();
        if(searchUserId != null){
            starModels = starSingleService.search(fetchIds,searchUserId);
        }
        for (ColumnDetailModel detailModel : detailModels){
            // 设置对应的作者
            for (UserDetailModel userDetailModel : userDetailModels){
                if(detailModel.getCreatedUserId().equals(userDetailModel.getId())){
                    detailModel.setAuthor(userDetailModel);
                    break;
                }
            }
            // 设置路径
            for (SysPathModel pathModel : pathModels){
                if(pathModel.getId().equals(detailModel.getId())){
                    detailModel.setPath(pathModel.getPath());
                    break;
                }
            }
            List<TagDetailModel> detailTags = new LinkedList<>();
            // 获取标签对应的专栏
            for (TagDetailModel tagDetailModel : tagDetailModels){
                for (Map.Entry<Long, List<ColumnTagModel>> mapEntry : tagIdList.entrySet()){
                    for (ColumnTagModel columnTagModel : mapEntry.getValue()){
                        if(columnTagModel.getTagId().equals(tagDetailModel.getId())){
                            if(detailModel.getId().equals(columnTagModel.getColumnId())){
                                detailTags.add(tagDetailModel);
                            }
                        }
                    }
                }
            }
            // 设置是否关注
            for (ColumnStarModel starModel : starModels){
                if(starModel.getColumnId().equals(detailModel.getId())){
                    detailModel.setUseStar(true);
                    break;
                }
            }
            detailModel.setTags(detailTags);
        }

        return detailModels;
    }

    @Override
    public int count(HashSet<Long> ids,String name, Long userId,Long memberId, Boolean isDisabled, Boolean isReviewed, Boolean isDeleted, HashSet<Long> tags, Integer minArticles, Integer maxArticles, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip) {
        if(ids == null) ids = new HashSet<>();
        ids.addAll(formTags(tags));
        if(tags != null && ids.size() == 0){
            return 0;
        }
        return columnInfoDao.count(ids,name,userId,memberId,isDisabled,isReviewed,isDeleted,minArticles,maxArticles,minStars,maxStars,beginTime,endTime,ip);
    }

    @Override
    public PageModel<ColumnDetailModel> page(HashSet<Long> ids, String name, Long userId,Long memberId, Long searchUserId, Boolean isDisabled, Boolean isReviewed, Boolean isDeleted, HashSet<Long> tags, Integer minArticles, Integer maxArticles, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;

        List<ColumnDetailModel> data = search(ids,name,userId,memberId,searchUserId,isDisabled,isReviewed,isDeleted,tags,minArticles,maxArticles,minStars,maxStars,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(ids,name,userId,memberId,isDisabled,isReviewed,isDeleted,tags,minArticles,maxArticles,minStars,maxStars,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public ColumnDetailModel getDetail(Long id) {
        return getDetail(id,null,null);
    }

    @Override
    public ColumnDetailModel getDetail(Long id, Long userId, Long searchUserId) {
        List<ColumnDetailModel> list = search(
                id != null ? new HashSet<Long>(){{add(id);}} : null,null,userId,null,searchUserId,null,null,null,null,null,null,null,null,null,null,null,null,0,1);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    @Override
    @Cacheable(unless = "#result == null")
    public ColumnInfoModel findById(Long id) {
        return columnInfoDao.findById(id);
    }

    @Override
    public ColumnInfoModel findByName(String name) {
        return columnInfoDao.findByName(name);
    }
}
