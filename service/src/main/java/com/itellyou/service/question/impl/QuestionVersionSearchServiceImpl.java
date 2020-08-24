package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionVersionDao;
import com.itellyou.model.article.ArticleVersionModel;
import com.itellyou.model.question.QuestionVersionModel;
import com.itellyou.model.question.QuestionVersionTagModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.question.QuestionVersionSearchService;
import com.itellyou.service.question.QuestionVersionTagService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.RedisUtils;
import com.itellyou.util.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = "question_version")
@Service
public class QuestionVersionSearchServiceImpl implements QuestionVersionSearchService {

    private final QuestionVersionDao versionDao;
    private final UserSearchService userSearchService;
    private final QuestionVersionTagService versionTagService;
    private final TagSearchService tagSearchService;

    public QuestionVersionSearchServiceImpl(QuestionVersionDao versionDao, UserSearchService userSearchService, QuestionVersionTagService versionTagService, TagSearchService tagSearchService) {
        this.versionDao = versionDao;
        this.userSearchService = userSearchService;
        this.versionTagService = versionTagService;
        this.tagSearchService = tagSearchService;
    }

    @Override
    public Integer findVersionById(Long id) {
        return versionDao.findVersionById(id);
    }

    @Override
    public List<QuestionVersionModel> searchByQuestionId(Long questionId, Boolean hasContent) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return versionDao.search(null,questionId != null ? new HashMap<Long,Integer>(){{ put(questionId,null);}} : null,null,hasContent,null,null,null,null,null,null,order,null,null);
    }

    @Override
    public List<QuestionVersionModel> searchByQuestionId(Long questionId){
        return searchByQuestionId(questionId,false);
    }

    @Override
    public List<QuestionVersionModel> searchByQuestionMap(Map<Long, Integer> questionMap, Boolean hasContent) {
        return search(null,questionMap,null,null,hasContent,null,null,null,null,null,null,null,null);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public QuestionVersionModel findById(Long id) {
        return findByQuestionIdAndId(id,null);
    }

    @Override
    public QuestionVersionModel findByQuestionIdAndId(Long id, Long questionId) {
        List<QuestionVersionModel> list = versionDao.search(id != null ? new HashSet<Long>(){{add(id);}} : null,questionId != null ? new HashMap<Long,Integer>(){{ put(questionId,null);}} : null,null,true,null,null,null,null,null,null,null,null,null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }

    @Override
    public List<QuestionVersionModel> search(HashSet<Long> ids, Map<Long, Integer> questionMap, Long userId, Boolean hasContent, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionVersionModel> versionModels = RedisUtils.fetchByCache("question_version", QuestionVersionModel.class,ids,(HashSet<Long> fetchIds) ->
                versionDao.search(fetchIds,questionMap,userId,hasContent,isReview,isDisable,isPublish,beginTime,endTime,ip,order,offset,limit),
                (QuestionVersionModel obj, Long id) -> id != null && obj.cacheKey().equals(id.toString()) && (hasContent != null && hasContent == true ? StringUtils.isNotEmpty(obj.getContent()) : true)
        );
        if(versionModels.size() == 0) return versionModels;
        HashSet<Long> authorIds = new LinkedHashSet<>();
        HashSet<Long> fetchIds = new LinkedHashSet<>();
        for (QuestionVersionModel versionModel : versionModels){
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
        Map<Long, List<QuestionVersionTagModel>> tagVersionIdList = versionTagService.searchTags(fetchIds);
        for (Map.Entry<Long, List<QuestionVersionTagModel>> mapEntry : tagVersionIdList.entrySet()){
            for (QuestionVersionTagModel questionVersionTagModel : mapEntry.getValue()){
                tagIds.add(questionVersionTagModel.getTagId());
            }
        }
        // 一次查出需要的标签
        List<TagDetailModel> tagDetailModels = tagIds.size() > 0 ? tagSearchService.search(tagIds,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        for (QuestionVersionModel versionModel : versionModels){
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
                for (Map.Entry<Long, List<QuestionVersionTagModel>> mapEntry : tagVersionIdList.entrySet()) {
                    for (QuestionVersionTagModel versionTagModel : mapEntry.getValue()) {
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
    public Integer count(HashSet<Long> ids, Map<Long, Integer> questionMap, Long userId, Boolean isReview, Boolean isDisable, Boolean isPublish, Long beginTime, Long endTime, Long ip) {
        return versionDao.count(ids,questionMap,userId,isReview,isDisable,isPublish,beginTime,endTime,ip);
    }
}
