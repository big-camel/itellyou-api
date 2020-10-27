package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleInfoDao;
import com.itellyou.model.article.*;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.*;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.service.article.*;
import com.itellyou.service.sys.EntitySearchService;
import com.itellyou.service.sys.EntityService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = CacheKeys.ARTICLE_KEY)
@Service
public class ArticleSearchServiceImpl implements ArticleSearchService , EntitySearchService<ArticleDetailModel> {

    private final ArticleInfoDao articleInfoDao;
    private final ArticlePaidReadSearchService paidReadSearchService;
    private final ArticleVersionSearchService versionSearchService;
    private final ArticleTagService articleTagService;
    private final TagSearchService tagSearchService;
    private final ArticleVersionTagService versionTagService;
    private final ArticleStarSingleService starSingleService;
    private final ArticleVoteService articleVoteService;
    private final ArticleSingleService singleService;
    private final EntityService entityService;
    private final ArticleVersionSingleService versionSingleService;

    @Autowired
    public ArticleSearchServiceImpl(ArticleInfoDao articleInfoDao, ArticlePaidReadSearchService paidReadSearchService, ArticleVersionSearchService versionSearchService, ArticleTagService articleTagService, TagSearchService tagSearchService, ArticleVersionTagService versionTagService, ArticleStarSingleService starSingleService, ArticleVoteService articleVoteService, ArticleSingleService singleService, EntityService entityService, ArticleVersionSingleService versionSingleService){
        this.articleInfoDao = articleInfoDao;
        this.paidReadSearchService = paidReadSearchService;
        this.versionSearchService = versionSearchService;
        this.articleTagService = articleTagService;
        this.tagSearchService = tagSearchService;
        this.versionTagService = versionTagService;
        this.starSingleService = starSingleService;
        this.articleVoteService = articleVoteService;
        this.singleService = singleService;
        this.entityService = entityService;
        this.versionSingleService = versionSingleService;
    }

    private Collection<Long> formTags(Collection<Long> tags){
        if(tags != null && tags.size() > 0){
            return articleTagService.searchArticleIds(tags);
        }
        return new HashSet<>();
    }

    @Override
    public List<ArticleDetailModel> search(Collection<Long> ids, String mode, Long columnId, Long userId, Long searchUserId, ArticleSourceType sourceType, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
                                           Collection<Long> tags, Integer minComment, Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {

        if(ids == null) ids = new HashSet<>();
        ids.addAll(formTags(tags));
        if(tags != null && ids.size() == 0){
            return new LinkedList<>();
        }
        List<ArticleInfoModel> infoModels = singleService.search(ids,mode,columnId,userId,sourceType,isDisabled,isPublished,isDeleted, minComment, maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,ip,order,offset,limit);

        List<ArticleDetailModel> detailModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;

        EntityDataModel<CacheEntity> entityDataModel = entityService.search(infoModels,(ArticleInfoModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.COLUMN);
            Collection columnIds = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!columnIds.contains(model.getColumnId()) && model.getColumnId() != null && model.getColumnId() > 0) columnIds.add(model.getColumnId());
            args.put("ids",columnIds);
            args.put("searchUserId",searchUserId);
            return new EntitySearchModel(EntityType.COLUMN,args);
        },(ArticleInfoModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.USER);
            Collection authorIds = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!authorIds.contains(model.getCreatedUserId())) authorIds.add(model.getCreatedUserId());
            args.put("ids",authorIds);
            return new EntitySearchModel(EntityType.USER,args);
        });

        Collection<Long> fetchIds = new LinkedHashSet<>();
        Map<Long,Integer> versionMap = new LinkedHashMap<>();
        List<ArticlePaidReadModel> paidReadModels = new LinkedList<>();
        for (ArticleInfoModel infoModel : infoModels){
            ArticleDetailModel detailModel = new ArticleDetailModel(infoModel);
            // 获取内容
            if(hasContent == null || hasContent == true  || mode == "draft"){
                versionMap.put(infoModel.getId(),!"draft".equals(mode) ? infoModel.getVersion() : infoModel.getDraft());
            }
            fetchIds.add(infoModel.getId());
            // 获取专栏
            detailModel.setColumn(entityDataModel.get(EntityType.COLUMN,infoModel.getColumnId()));
            // 获取作者
            detailModel.setAuthor(entityDataModel.get(EntityType.USER,infoModel.getCreatedUserId()));

            if(searchUserId != null){
                boolean isEquals = infoModel.getCreatedUserId().equals(searchUserId);

                detailModel.setAllowEdit(isEquals);
                detailModel.setAllowOppose(!isEquals);
                detailModel.setAllowStar(!isEquals);
                detailModel.setAllowSupport(!isEquals);
            }

            detailModels.add(detailModel);
        }
        // 一次查出需要的付费设置
        paidReadModels = paidReadSearchService.search(fetchIds);
        // 一次查出需要的版本信息
        List<ArticleVersionModel> versionModels = versionMap.size() > 0 ? versionSingleService.searchByArticleMap(versionMap,hasContent) : new ArrayList<>();
        LinkedHashSet<Long> versionIds = versionModels.stream().map(ArticleVersionModel::getId).collect(Collectors.toCollection(LinkedHashSet::new));
        // 一次查出需要的标签id列表
        Collection<Long> tagIds = new LinkedHashSet<>();
        Map<Long, List<ArticleVersionTagModel>> tagVersionIdList = new HashMap<>();
        Map<Long, List<ArticleTagModel>> tagArticleIdList = new HashMap<>();
        if("draft".equals(mode) && versionIds.size() > 0){
            tagVersionIdList = versionTagService.searchTags(versionIds);
            tagVersionIdList.values().forEach(models -> tagIds.addAll(models.stream().map(ArticleVersionTagModel::getTag).collect(Collectors.toSet())));
        }
        else if(fetchIds.size() > 0){
            tagArticleIdList = articleTagService.searchTags(fetchIds);
            tagArticleIdList.values().forEach(models -> tagIds.addAll(models.stream().map(ArticleTagModel::getTagId).collect(Collectors.toSet())));
        }

        // 一次查出需要的标签
        List<TagDetailModel> tagDetailModels = tagIds.size() > 0 ? tagSearchService.search(tagIds,null,null,null,null,searchUserId,false,null,null,null,null,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出是否有关注,是否点赞
        List<ArticleStarModel> starModels = searchUserId != null ? starSingleService.search(fetchIds,searchUserId) : new ArrayList<>();
        List<ArticleVoteModel> voteModels = searchUserId != null ? articleVoteService.search(fetchIds,searchUserId) : new ArrayList<>();

        for (ArticleDetailModel detailModel : detailModels){
            // 设置版本信息
            Optional<ArticleVersionModel> versionModelOptional = versionModels.stream().filter(model -> model.getArticleId().equals(detailModel.getId())).findFirst();

            versionModelOptional.ifPresent(versionModel -> {
                detailModel.setHtml(versionModel.getHtml());
                detailModel.setContent(versionModel.getContent());
                if (mode == "draft") {
                    detailModel.setTitle(versionModel.getTitle());
                    detailModel.setDescription(versionModel.getDescription());
                    detailModel.setSourceType(versionModel.getSourceType());
                    detailModel.setSourceData(versionModel.getSourceData());
                    detailModel.setColumnId(versionModel.getColumnId());
                    detailModel.setUpdatedIp(versionModel.getCreatedIp());
                    detailModel.setUpdatedTime(versionModel.getCreatedTime());
                    detailModel.setUpdatedUserId(versionModel.getCreatedUserId());
                }
            });

            // 付费设置
            for(ArticlePaidReadModel paidReadModel : paidReadModels) {
                if (paidReadModel.getArticleId().equals(detailModel.getId())) {
                    detailModel.setPaidRead(paidReadModel);
                    if (mode != "draft" && paidReadSearchService.checkRead(paidReadModel, detailModel.getCreatedUserId(), searchUserId) == false) {
                        String content = HtmlUtils.subEditorContent(detailModel.getContent(), detailModel.getHtml(), paidReadModel.getFreeReadScale());
                        detailModel.setContent(content);
                        String description;
                        if (StringUtils.isEmpty(detailModel.getCustomDescription())) {
                            String html = detailModel.getHtml();
                            if (hasContent != null && hasContent == false) {
                                ArticleVersionModel versionModel = versionSingleService.find(detailModel.getId(), detailModel.getVersion());
                                if (versionModel != null) html = versionModel.getHtml();
                            }

                            String text = StringUtils.removeHtmlTags(html);
                            int len = new BigDecimal(text.length()).multiply(new BigDecimal(paidReadModel.getFreeReadScale())).intValue();
                            if (len >= text.length()) len = text.length() - 1;
                            if (len <= 0) description = "";
                            else {
                                description = text.substring(0, len);
                                description = StringUtils.getFragmenter(description);
                            }
                            detailModel.setDescription(description);
                        } else {
                            detailModel.setDescription(null);
                        }
                        detailModel.setHtml(null);
                    } else {
                        detailModel.setPaidRead(null);
                    }
                }
                break;
            }

            List<TagDetailModel> detailTags = new LinkedList<>();
            // 获取文章对应的标签
            for (TagDetailModel tagDetailModel : tagDetailModels){
                if("draft".equals(mode)) {
                    for (Map.Entry<Long, List<ArticleVersionTagModel>> mapEntry : tagVersionIdList.entrySet()) {
                        for (ArticleVersionTagModel articleVersionTagModel : mapEntry.getValue()) {
                            if (articleVersionTagModel.getTag().equals(tagDetailModel.getId())) {
                                Long versionId = articleVersionTagModel.getVersion();
                                for(ArticleVersionModel versionModel : versionModels){
                                    if(versionId.equals(versionModel.getId())){
                                        if(detailModel.getId().equals(versionModel.getArticleId())){
                                            detailTags.add(tagDetailModel);
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }else{
                    for (Map.Entry<Long, List<ArticleTagModel>> mapEntry : tagArticleIdList.entrySet()) {
                        for (ArticleTagModel articleTagModel : mapEntry.getValue()) {
                            if (articleTagModel.getTagId().equals(tagDetailModel.getId())) {
                                if(detailModel.getId().equals(articleTagModel.getArticleId())){
                                    detailTags.add(tagDetailModel);
                                }
                            }
                        }
                    }
                }
            }
            detailModel.setTags(detailTags);
            // 设置是否关注
            detailModel.setUseStar(starModels.stream().filter(starModel -> starModel.getArticleId().equals(detailModel.getId())).findFirst().isPresent());
            // 获取是否点赞
            for(ArticleVoteModel voteModel : voteModels){
                if(voteModel.getArticleId().equals(detailModel.getId())){
                    detailModel.setUseOppose(VoteType.OPPOSE.equals(voteModel.getType()));
                    detailModel.setUseSupport(VoteType.SUPPORT.equals(voteModel.getType()));
                }
            }
        }
        return detailModels;
    }

    @Override
    public int count(Collection<Long> ids, String mode,Long columnId, Long userId, ArticleSourceType sourceType, Boolean isDisabled,Boolean isDeleted, Boolean isPublished,Collection<Long> tags,Integer minComment,Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip) {
        if(ids == null) ids = new HashSet<>();
        ids.addAll(formTags(tags));
        if(tags != null && ids.size() == 0){
            return 0;
        }
        return articleInfoDao.count(ids,mode,columnId,userId,sourceType,isDisabled,isPublished,isDeleted,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,ip);
    }

    @Override
    public List<ArticleDetailModel> search(Collection<Long> ids, String mode,Long columnId, Long userId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(ids,mode,columnId,userId,searchUserId,sourceType,hasContent,null,null,null,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public List<ArticleDetailModel> search(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished,
                                           Collection<Long> tags,Integer minComment,Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,null,columnId,null,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,tags,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public List<ArticleDetailModel> search(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished,Collection<Long> tags,Integer minComment,Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(columnId,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,tags,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Long columnId, ArticleSourceType sourceType,Boolean isDisabled,Boolean isDeleted, Boolean isPublished,Collection<Long> tags,Integer minComment,Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime) {
        return count(null,null,columnId,null,sourceType,isDisabled,isDeleted,isPublished,tags,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,null);
    }

    @Override
    public List<ArticleDetailModel> search(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(columnId,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<ArticleDetailModel> search(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(columnId,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Long columnId, ArticleSourceType sourceType,Boolean isDisabled,Boolean isDeleted, Boolean isPublished, Long beginTime, Long endTime) {
        return count(columnId,sourceType,isDisabled,isDeleted,isPublished,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime);
    }

    @Override
    public PageModel<ArticleDetailModel> page(Collection<Long> ids, String mode, Long columnId, Long userId, Long searchUserId, ArticleSourceType sourceType, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished, Collection<Long> tags, Integer minComment, Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<ArticleDetailModel> data = search(ids,mode,columnId,userId,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,tags,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(ids,mode,columnId,userId,sourceType,isDisabled,isDeleted,isPublished,tags,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public PageModel<ArticleDetailModel> page(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled, Boolean isDeleted, Boolean isPublished, Collection<Long> tags,Integer minComment, Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Map<String, String> order,Integer offset, Integer limit) {
        return page(null,null,columnId,null,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,tags,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public PageModel<ArticleDetailModel> page(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished, Long beginTime, Long endTime, Integer offset, Integer limit) {
        return page(columnId,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,null,offset,limit);
    }

    @Override
    public ArticleDetailModel getDetail(Long id, String mode, Long userId) {
        List<ArticleDetailModel> listArticle = search(id != null ? new HashSet<Long>(){{add(id);}} : null,mode,null,userId,null,null,null,null,null,null,0,1);
        return listArticle != null && listArticle.size() > 0 ? listArticle.get(0) : null;
    }

    @Override
    public ArticleDetailModel getDetail(Long id, String mode) {
        return getDetail(id,mode,null);
    }

    @Override
    public ArticleDetailModel getDetail(Long id, Long userId, Long searchUserId) {
        List<ArticleDetailModel> listArticle = search(id != null ? new HashSet<Long>(){{add(id);}} : null,null,null,userId,searchUserId,null,null,null,null,null,0,1);
        return listArticle != null && listArticle.size() > 0 ? listArticle.get(0) : null;
    }

    @Override
    public ArticleDetailModel getDetail(Long id) {
        return getDetail(id,(String) null);
    }

    @Override
    public List<ArticleTotalDetailModel> totalByUser(Collection<Long> userIds,Long searchUserId, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<ArticleTotalModel> infoModels = singleService.totalByUser(userIds,isDisabled,isPublished,isDeleted,beginTime,endTime,order,offset,limit);
        List<ArticleTotalDetailModel> detailModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;
        EntityDataModel<CacheEntity> entityDataModel = entityService.search(infoModels,(ArticleTotalModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.USER);
            Collection authorIds = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!authorIds.contains(model.getUserId())) authorIds.add(model.getUserId());
            args.put("ids",authorIds);
            args.put("searchUserId",searchUserId);
            return new EntitySearchModel(EntityType.USER,args);
        });
        for(ArticleTotalModel totalModel : infoModels){
            ArticleTotalDetailModel detailModel = new ArticleTotalDetailModel(totalModel);
            // 获取作者
            detailModel.setUser(entityDataModel.get(EntityType.USER,totalModel.getUserId()));
            detailModels.add(detailModel);
        }
        return detailModels;
    }

    @Override
    public ArticleDetailModel getDetail(Long id, Long userId) {
        return getDetail(id,(String) null,userId);
    }

    @Override
    public List<ArticleDetailModel> search(Map<String, Object> args) {
        Params params = new Params(args);
        return search(params.get("ids",Collection.class),
                params.get("mode",String.class),
                params.get("columnId",Long.class),
                params.get("userId",Long.class),
                params.get("searchUserId",Long.class),
                params.get("sourceType",ArticleSourceType.class),
                params.get("hasContent",Boolean.class),
                params.get("isDisabled",Boolean.class),
                params.get("isDeleted",Boolean.class),
                params.get("isPublished",Boolean.class),
                params.get("tags",Collection.class),
                params.get("minComment",Integer.class),
                params.get("maxComment",Integer.class),
                params.get("minView",Integer.class),
                params.get("maxView",Integer.class),
                params.get("minSupport",Integer.class),
                params.get("maxSupport",Integer.class),
                params.get("minOppose",Integer.class),
                params.get("maxOppose",Integer.class),
                params.get("minStar",Integer.class),
                params.get("maxStar",Integer.class),
                params.get("ip",Long.class),
                params.get("beginTime",Long.class),
                params.get("endTime",Long.class),
                params.get("order",Map.class),
                params.get("offset",Integer.class),
                params.get("limit",Integer.class));
    }
}
