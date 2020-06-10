package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleVersionDao;
import com.itellyou.model.article.*;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.article.ArticleVersionSearchService;
import com.itellyou.service.article.ArticleVersionTagService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = "article_version")
@Service
public class ArticleVersionSearchServiceImpl implements ArticleVersionSearchService {

    private final ArticleVersionDao versionDao;
    private final UserSearchService userSearchService;
    private final ArticleVersionTagService versionTagService;
    private final TagSearchService tagSearchService;

    public ArticleVersionSearchServiceImpl(ArticleVersionDao versionDao, UserSearchService userSearchService, ArticleVersionTagService versionTagService, TagSearchService tagSearchService) {
        this.versionDao = versionDao;
        this.userSearchService = userSearchService;
        this.versionTagService = versionTagService;
        this.tagSearchService = tagSearchService;
    }

    @Override
    @Cacheable(key = "T(String).valueOf(#articleId).concat('-').concat(#version)",unless = "#result == null")
    public ArticleVersionModel find(Long articleId, Integer version) {
        return versionDao.findByArticleIdAndVersion(articleId,version,true);
    }

    @Override
    public List<ArticleVersionModel> search(HashSet<Long> ids, Map<Long, Integer> articleMap, Long userId, ArticleSourceType sourceType, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<ArticleVersionModel> versionModels = RedisUtils.fetchByCache("article_version", ArticleVersionModel.class,ids,(HashSet<Long> fetchIds) ->
                versionDao.search(fetchIds,articleMap,userId,sourceType,hasContent,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit)
        );
        if(versionModels.size() == 0) return versionModels;
        HashSet<Long> authorIds = new LinkedHashSet<>();
        HashSet<Long> fetchIds = new LinkedHashSet<>();
        for (ArticleVersionModel versionModel : versionModels){
            fetchIds.add(versionModel.getId());
            if(!authorIds.contains(versionModel.getCreatedUserId())) authorIds.add(versionModel.getCreatedUserId());
        }
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = authorIds.size() > 0 ? userSearchService.search(authorIds,null,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出需要的标签id列表
        HashSet<Long> tagIds = new LinkedHashSet<>();
        Map<Long, List<ArticleVersionTagModel>> tagVersionIdList = versionTagService.searchTags(fetchIds);
        for (Map.Entry<Long, List<ArticleVersionTagModel>> mapEntry : tagVersionIdList.entrySet()){
            for (ArticleVersionTagModel articleVersionTagModel : mapEntry.getValue()){
                tagIds.add(articleVersionTagModel.getTag());
            }
        }
        // 一次查出需要的标签
        List<TagDetailModel> tagDetailModels = tagIds.size() > 0 ? tagSearchService.search(tagIds,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();

        for (ArticleVersionModel versionModel : versionModels){
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
                for (Map.Entry<Long, List<ArticleVersionTagModel>> mapEntry : tagVersionIdList.entrySet()) {
                    for (ArticleVersionTagModel versionTagModel : mapEntry.getValue()) {
                        if (versionTagModel.getTag().equals(tagDetailModel.getId())) {
                            versionId = versionTagModel.getVersion();
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
    public Integer count(HashSet<Long> ids, Map<Long, Integer> articleMap, Long userId, ArticleSourceType sourceType, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip) {
        return versionDao.count(ids,articleMap,userId,sourceType,isReview,isDisable,isPublish,beginTime,endTime,ip);
    }

    @Override
    public Integer findVersionById(Long id) {
        return versionDao.findVersionById(id);
    }

    @Override
    public List<ArticleVersionModel> searchByArticleId(Long articleId, Boolean hasContent) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(null,articleId != null ? new HashMap<Long,Integer>(){{ put(articleId,null);}} : null,null,null,hasContent,null,null,null,null,null,null,order,null,null);
    }

    @Override
    public List<ArticleVersionModel> searchByArticleId(Long articleId){
        return searchByArticleId(articleId,false);
    }

    @Override
    public List<ArticleVersionModel> searchByArticleMap(Map<Long, Integer> articleMap, Boolean hasContent) {
        return search(null,articleMap,null,null,hasContent,null,null,null,null,null,null,null,null,null);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public ArticleVersionModel findById(Long id) {
        return findByArticleIdAndId(id,null);
    }

    @Override
    public ArticleVersionModel findByArticleIdAndId(Long id, Long articleId) {
        List<ArticleVersionModel> list = search(id != null ? new HashSet<Long>(){{add(id);}} : null, articleId != null ? new HashMap<Long,Integer>() {{ put(articleId,null);}} : null, null, null, true, null, null, null, null, null, null, null, null, null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }
}
