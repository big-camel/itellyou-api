package com.itellyou.service.column.impl;

import com.itellyou.dao.column.ColumnInfoDao;
import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.column.ColumnStarModel;
import com.itellyou.model.column.ColumnTagModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.*;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.service.column.ColumnSearchService;
import com.itellyou.service.column.ColumnStarSingleService;
import com.itellyou.service.column.ColumnTagService;
import com.itellyou.service.sys.EntitySearchService;
import com.itellyou.service.sys.EntityService;
import com.itellyou.service.sys.SysPathService;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.Params;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = CacheKeys.COLUMN_KEY)
@Service
public class ColumnSearchServiceImpl implements ColumnSearchService , EntitySearchService<ColumnDetailModel> {

    private final ColumnInfoDao columnInfoDao;
    private final SysPathService pathService;
    private final ColumnTagService columnTagService;
    private final ColumnStarSingleService starSingleService;
    private final EntityService entityService;

    public ColumnSearchServiceImpl(ColumnInfoDao columnInfoDao, SysPathService pathService, ColumnTagService columnTagService, ColumnStarSingleService starSingleService, EntityService entityService){
        this.columnInfoDao = columnInfoDao;
        this.pathService = pathService;
        this.columnTagService = columnTagService;
        this.starSingleService = starSingleService;
        this.entityService = entityService;
    }

    private Collection<Long> formTags(Collection<Long> tags){
        if(tags != null && tags.size() > 0){
            return columnTagService.searchColumnIds(tags);
        }
        return new HashSet<>();
    }

    @Override
    public List<ColumnDetailModel> search(Collection<Long> ids, String name, Long userId,Long memberId, Long searchUserId, Boolean isDisabled, Boolean isReviewed, Boolean isDeleted, Collection<Long> tags, Integer minArticles, Integer maxArticles, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(ids == null) ids = new HashSet<>();
        ids.addAll(formTags(tags));
        if(tags != null && ids.size() == 0){
            return new LinkedList<>();
        }
        List<ColumnInfoModel> infoModels = RedisUtils.fetch(CacheKeys.COLUMN_KEY,ColumnInfoModel.class,ids,(Collection<Long> fetchIds) ->
                columnInfoDao.search(fetchIds,name,userId,memberId,searchUserId,isDisabled,isReviewed,isDeleted,minArticles,maxArticles,minStars,maxStars,beginTime,endTime,ip,order,offset,limit)
        );
        List<ColumnDetailModel> detailModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;

        Collection<Long> fetchIds = new LinkedHashSet<>();
        // 转换为详细model
        for (ColumnInfoModel infoModel : infoModels) {
            ColumnDetailModel detailModel = new ColumnDetailModel(infoModel);
            // 添加需要查询id
            fetchIds.add(infoModel.getId());
            detailModels.add(detailModel);
        }
        // 一次查出需要的标签
        Collection<Long> tagIds = new LinkedHashSet<>();
        Map<Long,List<ColumnTagModel>> columnTagModelMap = columnTagService.searchTags(fetchIds);
        columnTagModelMap.values().forEach(columnTagModels -> tagIds.addAll(columnTagModels.stream().map(ColumnTagModel::getTagId).collect(Collectors.toCollection(LinkedHashSet::new))));
        EntitySearchModel tagSearchModel = new EntitySearchModel(EntityType.TAG,new HashMap<String,Object>(){{
            put("ids",tagIds);
            put("hasContent",false);
        }});
        EntityDataModel<CacheEntity> entityDataModel = entityService.search(infoModels,(ColumnInfoModel model, Function<EntityType,Map<String,Object>> getArgs) -> tagSearchModel, (ColumnInfoModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.USER);
            Collection authorIds = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!authorIds.contains(model.getCreatedUserId())) authorIds.add(model.getCreatedUserId());
            args.put("ids",authorIds);
            return new EntitySearchModel(EntityType.USER,args);
        });
        // 一次获取路径信息
        List<SysPathModel> pathModels = fetchIds.size() > 0 ? pathService.search(SysPath.COLUMN,fetchIds) : new ArrayList<>();
        // 一次查出是否有关注
        List<ColumnStarModel> starModels = new ArrayList<>();
        if(searchUserId != null){
            starModels = starSingleService.search(fetchIds,searchUserId);
        }
        for (ColumnDetailModel detailModel : detailModels){
            // 设置对应的作者
            detailModel.setAuthor(entityDataModel.get(EntityType.USER,detailModel.getCreatedUserId()));
            // 设置路径
            Optional<SysPathModel> pathModelOptional = pathModels.stream().filter(pathModel -> pathModel.getId().equals(detailModel.getId())).findFirst();
            if(pathModelOptional.isPresent()) detailModel.setPath(pathModelOptional.get().getPath());
            // 获取标签对应的专栏
            List<ColumnTagModel> columnTagModels = columnTagModelMap.computeIfAbsent(detailModel.getId(),key -> new LinkedList<>());
            Collection<TagDetailModel> tagDetailModels = entityDataModel.get(EntityType.TAG);
            List<TagDetailModel> detailTags = tagDetailModels == null ? new ArrayList<>() : tagDetailModels.stream().filter(tagModel -> {
                if(columnTagModels.stream().filter(columnTagModel -> columnTagModel.getTagId().equals(tagModel.cacheKey())).findFirst().isPresent())
                    return true;
                return false;
            }).collect(Collectors.toList());
            detailModel.setTags(detailTags);
            // 设置是否关注
            detailModel.setUseStar(starModels.stream().filter(starModel -> starModel.getColumnId().equals(detailModel.getId())).findFirst().isPresent());
        }

        return detailModels;
    }

    @Override
    public int count(Collection<Long> ids,String name, Long userId,Long memberId, Boolean isDisabled, Boolean isReviewed, Boolean isDeleted, Collection<Long> tags, Integer minArticles, Integer maxArticles, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip) {
        if(ids == null) ids = new HashSet<>();
        ids.addAll(formTags(tags));
        if(tags != null && ids.size() == 0){
            return 0;
        }
        return columnInfoDao.count(ids,name,userId,memberId,isDisabled,isReviewed,isDeleted,minArticles,maxArticles,minStars,maxStars,beginTime,endTime,ip);
    }

    @Override
    public PageModel<ColumnDetailModel> page(Collection<Long> ids, String name, Long userId,Long memberId, Long searchUserId, Boolean isDisabled, Boolean isReviewed, Boolean isDeleted, Collection<Long> tags, Integer minArticles, Integer maxArticles, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
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

    @Override
    public List<ColumnDetailModel> search(Map<String, Object> args) {
        Params params = new Params(args);
        return search(params.get("ids",Collection.class),
                params.get("name",String.class),
                params.get("userId",Long.class),
                params.get("memberId",Long.class),
                params.get("searchUserId",Long.class),
                params.get("isDisabled",Boolean.class),
                params.get("isReviewed",Boolean.class),
                params.get("isDeleted",Boolean.class),
                params.get("tags",Collection.class),
                params.get("minArticles",Integer.class),
                params.get("maxArticles",Integer.class),
                params.get("minStar",Integer.class),
                params.get("maxStar",Integer.class),
                params.get("beginTime",Long.class),
                params.get("endTime",Long.class),
                params.get("ip",Long.class),
                params.get("order",Map.class),
                params.get("offset",Integer.class),
                params.get("limit",Integer.class));
    }
}
