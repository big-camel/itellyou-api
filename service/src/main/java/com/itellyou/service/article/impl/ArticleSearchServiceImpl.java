package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleInfoDao;
import com.itellyou.model.article.*;
import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.article.*;
import com.itellyou.service.column.ColumnSearchService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.HtmlUtils;
import com.itellyou.util.RedisUtils;
import com.itellyou.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@CacheConfig(cacheNames = "article")
@Service
public class ArticleSearchServiceImpl implements ArticleSearchService {

    private final ArticleInfoDao articleInfoDao;
    private final ArticlePaidReadSearchService paidReadSearchService;
    private final ArticleVersionSearchService versionSearchService;
    private final ArticleTagService articleTagService;
    private final TagSearchService tagSearchService;
    private final ArticleVersionTagService versionTagService;
    private final ColumnSearchService columnSearchService;
    private final UserSearchService userSearchService;
    private final ArticleStarSingleService starSingleService;
    private final ArticleVoteService articleVoteService;

    @Autowired
    public ArticleSearchServiceImpl(ArticleInfoDao articleInfoDao, ArticlePaidReadSearchService paidReadSearchService, ArticleVersionSearchService versionSearchService, ArticleTagService articleTagService, TagSearchService tagSearchService, ArticleVersionTagService versionTagService, ColumnSearchService columnSearchService, UserSearchService userSearchService, ArticleStarSingleService starSingleService, ArticleVoteService articleVoteService){
        this.articleInfoDao = articleInfoDao;
        this.paidReadSearchService = paidReadSearchService;
        this.versionSearchService = versionSearchService;
        this.articleTagService = articleTagService;
        this.tagSearchService = tagSearchService;
        this.versionTagService = versionTagService;
        this.columnSearchService = columnSearchService;
        this.userSearchService = userSearchService;
        this.starSingleService = starSingleService;
        this.articleVoteService = articleVoteService;
    }

    private HashSet<Long> formTags(HashSet<Long> tags){
        if(tags != null && tags.size() > 0){
            return articleTagService.searchArticleId(tags);
        }
        return new HashSet<>();
    }

    @Override
    public List<ArticleDetailModel> search(HashSet<Long> ids, String mode, Long columnId, Long userId, Long searchUserId, ArticleSourceType sourceType, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished,
                                           HashSet<Long> tags, Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {

        if(ids == null) ids = new HashSet<>();
        ids.addAll(formTags(tags));

        List<ArticleInfoModel> infoModels = RedisUtils.fetchByCache("article",ArticleInfoModel.class,ids,(HashSet<Long> fetchIds) ->
                        articleInfoDao.search(fetchIds,mode,columnId,userId,sourceType,isDisabled,isPublished,isDeleted, minComments, maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,ip,order,offset,limit)
                );
        List<ArticleDetailModel> detailModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;
        HashSet<Long> columnIds = new LinkedHashSet<>();
        HashSet<Long> authorIds = new LinkedHashSet<>();
        HashSet<Long> fetchIds = new LinkedHashSet<>();
        HashMap<Long,Integer> versionMap = new LinkedHashMap<>();
        List<ArticleVersionModel> versionModels = new LinkedList<>();
        List<ArticlePaidReadModel> paidReadModels = new LinkedList<>();
        for (ArticleInfoModel infoModel : infoModels){
            ArticleDetailModel detailModel = new ArticleDetailModel(infoModel);

            // 获取内容
            if(hasContent == null || hasContent == true  || mode == "draft"){
                versionMap.put(infoModel.getId(),!"draft".equals(mode) ? infoModel.getVersion() : infoModel.getDraft());
            }
            fetchIds.add(infoModel.getId());
            // 获取专栏
            if(infoModel.getColumnId() != null && infoModel.getColumnId() > 0 && !columnIds.contains(infoModel.getColumnId())){
                columnIds.add(infoModel.getColumnId());
            }
            // 获取作者
            if(!authorIds.contains(infoModel.getCreatedUserId())) authorIds.add(infoModel.getCreatedUserId());

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
        HashSet<Long> versionIds = new LinkedHashSet<>();
        versionModels = versionMap.size() > 0 ? versionSearchService.searchByArticleMap(versionMap,hasContent) : new ArrayList<>();
        for (ArticleVersionModel versionModel : versionModels){
            versionIds.add(versionModel.getId());
        }
        // 一次查出需要的专栏
        List<ColumnDetailModel> columnDetailModels = columnIds.size() > 0 ? columnSearchService.search(columnIds,null,null,null,searchUserId,null,null,null,null,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = authorIds.size() > 0 ? userSearchService.search(authorIds,searchUserId,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出需要的标签id列表
        HashSet<Long> tagIds = new LinkedHashSet<>();
        Map<Long, List<ArticleVersionTagModel>> tagVersionIdList = new HashMap<>();
        Map<Long, List<ArticleTagModel>> tagArticleIdList = new HashMap<>();
        if("draft".equals(mode) && versionIds.size() > 0){
            tagVersionIdList = versionTagService.searchTags(versionIds);
            for (Map.Entry<Long, List<ArticleVersionTagModel>> mapEntry : tagVersionIdList.entrySet()){
                for (ArticleVersionTagModel articleVersionTagModel : mapEntry.getValue()){
                    tagIds.add(articleVersionTagModel.getTag());
                }
            }
        }
        else if(fetchIds.size() > 0){
            tagArticleIdList = articleTagService.searchTags(fetchIds);
            for (Map.Entry<Long, List<ArticleTagModel>> mapEntry : tagArticleIdList.entrySet()){
                for (ArticleTagModel articleTagModel : mapEntry.getValue()){
                    tagIds.add(articleTagModel.getTagId());
                }
            }
        }
        // 一次查出需要的标签
        List<TagDetailModel> tagDetailModels = tagIds.size() > 0 ? tagSearchService.search(tagIds,null,null,null,null,searchUserId,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出是否有关注,是否点赞
        List<ArticleStarModel> starModels = new ArrayList<>();
        List<ArticleVoteModel> voteModels = new ArrayList<>();
        if(searchUserId != null){
            starModels = starSingleService.search(fetchIds,searchUserId);
            voteModels = articleVoteService.search(fetchIds,searchUserId);
        }

        for (ArticleDetailModel detailModel : detailModels){
            // 设置版本信息
            for(ArticleVersionModel versionModel : versionModels){
                if(versionModel.getArticleId().equals(detailModel.getId())) {
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
                    break;
                }
            }
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
                                ArticleVersionModel versionModel = versionSearchService.find(detailModel.getId(), detailModel.getVersion());
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
                Long articleId = null;
                if("draft".equals(mode)) {
                    for (Map.Entry<Long, List<ArticleVersionTagModel>> mapEntry : tagVersionIdList.entrySet()) {
                        for (ArticleVersionTagModel articleVersionTagModel : mapEntry.getValue()) {
                            if (articleVersionTagModel.getTag().equals(tagDetailModel.getId())) {
                                Long versionId = articleVersionTagModel.getVersion();
                                for(ArticleVersionModel versionModel : versionModels){
                                    if(versionId.equals(versionModel.getId())){
                                        articleId = versionModel.getArticleId();
                                        break;
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
                                articleId = articleTagModel.getArticleId();
                                break;
                            }
                        }
                    }
                }
                if(detailModel.getId().equals(articleId)){
                    detailTags.add(tagDetailModel);
                }
            }
            detailModel.setTags(detailTags);
            // 设置专栏
            for (ColumnDetailModel columnDetailModel : columnDetailModels){
                if(columnDetailModel.getId().equals(detailModel.getColumnId())){
                    detailModel.setColumn(columnDetailModel);
                    break;
                }
            }
            // 设置是否关注
            for (ArticleStarModel starModel : starModels){
                if(starModel.getArticleId().equals(detailModel.getId())){
                    detailModel.setUseStar(true);
                    break;
                }
            }
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
    public int count(HashSet<Long> ids, String mode,Long columnId, Long userId, ArticleSourceType sourceType, Boolean isDisabled,Boolean isDeleted, Boolean isPublished,HashSet<Long> tags,Integer minComments,Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip) {
        if(ids == null) ids = new HashSet<>();
        ids.addAll(formTags(tags));
        return articleInfoDao.count(ids,mode,columnId,userId,sourceType,isDisabled,isPublished,isDeleted,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,ip);
    }

    @Override
    public List<ArticleDetailModel> search(HashSet<Long> ids, String mode,Long columnId, Long userId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(ids,mode,columnId,userId,searchUserId,sourceType,hasContent,null,null,null,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public List<ArticleDetailModel> search(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished,
                                           HashSet<Long> tags,Integer minComments,Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,null,columnId,null,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,null,order,offset,limit);
    }

    @Override
    public List<ArticleDetailModel> search(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isPublished,HashSet<Long> tags,Integer minComments,Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(columnId,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Long columnId, ArticleSourceType sourceType,Boolean isDisabled,Boolean isDeleted, Boolean isPublished,HashSet<Long> tags,Integer minComments,Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime) {
        return count(null,null,columnId,null,sourceType,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,null);
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
    public PageModel<ArticleDetailModel> page(HashSet<Long> ids, String mode, Long columnId, Long userId, Long searchUserId, ArticleSourceType sourceType, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isPublished, HashSet<Long> tags, Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<ArticleDetailModel> data = search(ids,mode,columnId,userId,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(ids,mode,columnId,userId,sourceType,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public PageModel<ArticleDetailModel> page(Long columnId,Long searchUserId, ArticleSourceType sourceType,Boolean hasContent,Boolean isDisabled, Boolean isDeleted, Boolean isPublished, HashSet<Long> tags,Integer minComments, Integer maxComments, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStars, Integer maxStars, Long beginTime, Long endTime, Map<String, String> order,Integer offset, Integer limit) {
        return page(null,null,columnId,null,searchUserId,sourceType,hasContent,isDisabled,isDeleted,isPublished,tags,minComments,maxComments,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStars,maxStars,beginTime,endTime,null,order,offset,limit);
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
    public ArticleDetailModel getDetail(Long id, Long userId) {
        return getDetail(id,(String) null,userId);
    }
}
