package com.itellyou.service.article.impl;

import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.article.ArticleVersionDetailModel;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.model.article.ArticleVersionTagModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.EntityDataModel;
import com.itellyou.model.sys.EntitySearchModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.service.article.ArticleVersionSearchService;
import com.itellyou.service.article.ArticleVersionSingleService;
import com.itellyou.service.article.ArticleVersionTagService;
import com.itellyou.service.sys.EntityService;
import com.itellyou.util.CacheEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = CacheKeys.ARTICLE_VERSION_KEY)
@Service
public class ArticleVersionSearchServiceImpl implements ArticleVersionSearchService {

    private final ArticleVersionTagService versionTagService;
    private final ArticleVersionSingleService versionSingleService;
    private final EntityService entityService;

    public ArticleVersionSearchServiceImpl( ArticleVersionTagService versionTagService, ArticleVersionSingleService versionSingleService, EntityService entityService) {
        this.versionTagService = versionTagService;
        this.versionSingleService = versionSingleService;
        this.entityService = entityService;
    }

    @Override
    public List<ArticleVersionDetailModel> searchByArticleId(Long articleId, Boolean hasContent) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(null,articleId != null ? new HashMap<Long,Integer>(){{ put(articleId,null);}} : null,null,null,hasContent,null,null,null,null,null,null,order,null,null);
    }

    @Override
    public List<ArticleVersionDetailModel> searchByArticleId(Long articleId) {
        return searchByArticleId(articleId,false);
    }

    @Override
    public ArticleVersionDetailModel getDetail(Long id) {
        List<ArticleVersionDetailModel> list = search(new HashSet<Long>(){{add(id);}},null,null,null,true,null,null,null,null,null,null,null,null,null);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public ArticleVersionDetailModel getDetail(Long articleId, Integer version) {
        List<ArticleVersionDetailModel> list = search(null,new HashMap<Long, Integer>(){{put(articleId,version);}},null,null,true,null,null,null,null,null,null,null,null,null);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<ArticleVersionDetailModel> search(Collection<Long> ids, Map<Long, Integer> articleMap, Long userId, ArticleSourceType sourceType, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<ArticleVersionModel> versionModels = versionSingleService.search(ids,articleMap,userId,sourceType,hasContent,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit);
        List<ArticleVersionDetailModel> detailModels = new LinkedList<>();
        if(versionModels.size() == 0) return detailModels;

        Collection<Long> fetchIds = versionModels.stream().map(ArticleVersionModel::getId).collect(Collectors.toSet());
                // 一次查出需要的标签id列表
        Collection<Long> tagIds = new LinkedHashSet<>();
        Map<Long, List<ArticleVersionTagModel>> tagVersionIdList = versionTagService.searchTags(fetchIds);
        for (Map.Entry<Long, List<ArticleVersionTagModel>> mapEntry : tagVersionIdList.entrySet()){
            for (ArticleVersionTagModel articleVersionTagModel : mapEntry.getValue()){
                tagIds.add(articleVersionTagModel.getTag());
            }
        }

        EntityDataModel<CacheEntity> entityDataModel = entityService.search(versionModels,(ArticleVersionModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.TAG);
            args.put("ids",tagIds);
            return new EntitySearchModel(EntityType.TAG,args);
        },(ArticleVersionModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.USER);
            Collection authorIds = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!authorIds.contains(model.getCreatedUserId())) authorIds.add(model.getCreatedUserId());
            args.put("ids",authorIds);
            return new EntitySearchModel(EntityType.USER,args);
        });

        for (ArticleVersionModel versionModel : versionModels){
            ArticleVersionDetailModel detailModel = new ArticleVersionDetailModel(versionModel);
            detailModel.setAuthor(entityDataModel.get(EntityType.USER,versionModel.getCreatedUserId()));
            List<TagDetailModel> detailTags = new LinkedList<>();
            // 获取标签对应的文章
            for (Map.Entry<Long, List<ArticleVersionTagModel>> mapEntry : tagVersionIdList.entrySet()) {
                if(versionModel.getId().equals(mapEntry.getKey())){
                    for (ArticleVersionTagModel versionTagModel : mapEntry.getValue()) {
                        TagDetailModel tagDetailModel = entityDataModel.get(EntityType.TAG,versionTagModel.getTag());
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

}
