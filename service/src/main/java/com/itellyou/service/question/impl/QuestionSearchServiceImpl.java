package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionInfoDao;
import com.itellyou.model.question.*;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.RewardType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.question.*;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.*;

@CacheConfig(cacheNames = "question")
@Service
public class QuestionSearchServiceImpl implements QuestionSearchService {

    private final QuestionInfoDao questionInfoDao;
    private final QuestionTagService questionTagService;
    private final QuestionVersionSearchService versionSearchService;
    private final UserSearchService userSearchService;
    private final QuestionVersionTagService versionTagService;
    private final TagSearchService tagSearchService;
    private final QuestionStarSingleService starSingleService;
    private final QuestionAnswerSearchService answerSearchService;

    @Autowired
    public QuestionSearchServiceImpl(QuestionInfoDao questionInfoDao, QuestionTagService questionTagService, QuestionVersionSearchService versionSearchService, UserSearchService userSearchService, QuestionVersionTagService versionTagService, TagSearchService tagSearchService, QuestionStarSingleService starSingleService, QuestionAnswerSearchService answerSearchService){
        this.questionInfoDao = questionInfoDao;
        this.questionTagService = questionTagService;
        this.versionSearchService = versionSearchService;
        this.userSearchService = userSearchService;
        this.versionTagService = versionTagService;
        this.tagSearchService = tagSearchService;
        this.starSingleService = starSingleService;
        this.answerSearchService = answerSearchService;
    }

    private HashSet<Long> formTags(HashSet<Long> tags){
        if(tags != null && tags.size() > 0){
            return questionTagService.searchQuestionId(tags);
        }
        return new HashSet<>();
    }

    @Override
    public List<QuestionDetailModel> search(HashSet<Long> ids, String mode, Long userId, Long searchUserId, Boolean hasContent, Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long ip,
                                            Integer childCount,
                                            RewardType rewardType,
                                            Double minRewardValue,
                                            Double maxRewardValue, HashSet<Long> tags, Integer minComments, Integer maxComments, Integer minAnswers, Integer maxAnswers, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {

        if(ids == null) ids = new HashSet<>();
        ids.addAll(formTags(tags));

        List<QuestionInfoModel> infoModels = RedisUtils.fetchByCache("question",QuestionInfoModel.class,ids,(HashSet<Long> fetchIds) ->
                questionInfoDao.search(fetchIds,mode,userId,searchUserId,isDisabled,isAdopted,isPublished,isDeleted,ip,rewardType,minRewardValue,maxRewardValue,minComments,maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit)
        );
        List<QuestionDetailModel> detailModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;
        HashSet<Long> authorIds = new LinkedHashSet<>();
        HashSet<Long> fetchIds = new LinkedHashSet<>();
        HashMap<Long,Integer> versionMap = new LinkedHashMap<>();
        List<QuestionVersionModel> versionModels = new LinkedList<>();
        for (QuestionInfoModel infoModel : infoModels){
            QuestionDetailModel detailModel = new QuestionDetailModel(infoModel);

            // 获取内容
            if(hasContent == null || hasContent == true  || mode == "draft"){
                versionMap.put(infoModel.getId(),!"draft".equals(mode) ? infoModel.getVersion() : infoModel.getDraft());
            }
            fetchIds.add(infoModel.getId());

            // 获取作者
            if(!authorIds.contains(infoModel.getCreatedUserId())) authorIds.add(infoModel.getCreatedUserId());

            detailModels.add(detailModel);
        }

        // 一次查出需要的版本信息
        HashSet<Long> versionIds = new LinkedHashSet<>();
        versionModels = versionMap.size() > 0 ? versionSearchService.searchByQuestionMap(versionMap,hasContent) : new ArrayList<>();
        for (QuestionVersionModel versionModel : versionModels){
            versionIds.add(versionModel.getId());
        }
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = authorIds.size() > 0 ? userSearchService.search(authorIds,searchUserId,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出需要的标签id列表
        HashSet<Long> tagIds = new LinkedHashSet<>();
        Map<Long, List<QuestionVersionTagModel>> tagVersionIdList = new HashMap<>();
        Map<Long, List<QuestionTagModel>> tagQuestionIdList = new HashMap<>();
        if("draft".equals(mode) && versionIds.size() > 0){
            tagVersionIdList = versionTagService.searchTags(versionIds);
            for (Map.Entry<Long, List<QuestionVersionTagModel>> mapEntry : tagVersionIdList.entrySet()){
                for (QuestionVersionTagModel questionVersionTagModel : mapEntry.getValue()){
                    tagIds.add(questionVersionTagModel.getTagId());
                }
            }
        }
        else if(fetchIds.size() > 0){
            tagQuestionIdList = questionTagService.searchTags(fetchIds);
            for (Map.Entry<Long, List<QuestionTagModel>> mapEntry : tagQuestionIdList.entrySet()){
                for (QuestionTagModel questionTagModel : mapEntry.getValue()){
                    tagIds.add(questionTagModel.getTagId());
                }
            }
        }
        // 一次查出需要的标签
        List<TagDetailModel> tagDetailModels = tagIds.size() > 0 ? tagSearchService.search(tagIds,null,null,null,null,searchUserId,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出是否关注
        List<QuestionStarModel> starModels = new ArrayList<>();
        if(searchUserId != null){
            starModels = starSingleService.search(fetchIds,searchUserId);
        }
        // 一次查出需要的回答
        List<QuestionAnswerDetailModel> answerDetailModels = new LinkedList<>();
        if(childCount != null && childCount > 0){
            Map<String,String> answerOrder = new LinkedHashMap<>();
            answerOrder.put("is_adopted","desc");
            answerOrder.put("support","desc");
            answerOrder.put("comments","desc");
            // 不加会导致 childCount 失效
            answerOrder.put("id","asc");
            List<QuestionAnswerModel> answerModels = answerSearchService.searchChild(null,fetchIds,null,null,childCount,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,answerOrder);
            HashSet<Long> answerIds = new LinkedHashSet<>();
            for (QuestionAnswerModel answerModel : answerModels){
                answerIds.add(answerModel.getId());
            }
            answerDetailModels = answerSearchService.search(answerIds,null,null,searchUserId,null,null,null,null,null,null
            ,null);
        }
        for (QuestionDetailModel detailModel : detailModels){
            // 设置版本信息
            for(QuestionVersionModel versionModel : versionModels){
                if(versionModel.getQuestionId().equals(detailModel.getId())) {
                    detailModel.setHtml(versionModel.getHtml());
                    detailModel.setContent(versionModel.getContent());
                    if (mode == "draft") {
                        detailModel.setTitle(versionModel.getTitle());
                        detailModel.setDescription(versionModel.getDescription());
                        detailModel.setRewardType(versionModel.getRewardType());
                        detailModel.setRewardAdd(versionModel.getRewardAdd());
                        detailModel.setRewardValue(versionModel.getRewardValue());
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
            // 获取标签对应的问题
            for (TagDetailModel tagDetailModel : tagDetailModels){
                Long questionId = null;
                if("draft".equals(mode)) {
                    for (Map.Entry<Long, List<QuestionVersionTagModel>> mapEntry : tagVersionIdList.entrySet()) {
                        for (QuestionVersionTagModel questionVersionTagModel : mapEntry.getValue()) {
                            if (questionVersionTagModel.getTagId().equals(tagDetailModel.getId())) {
                                Long versionId = questionVersionTagModel.getVersionId();
                                for(QuestionVersionModel versionModel : versionModels){
                                    if(versionId.equals(versionModel.getId())){
                                        questionId = versionModel.getQuestionId();
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }else{
                    for (Map.Entry<Long, List<QuestionTagModel>> mapEntry : tagQuestionIdList.entrySet()) {
                        for (QuestionTagModel questionTagModel : mapEntry.getValue()) {
                            if (questionTagModel.getTagId().equals(tagDetailModel.getId())) {
                                questionId = questionTagModel.getQuestionId();
                                break;
                            }
                        }
                    }
                }
                if(detailModel.getId().equals(questionId)){
                    detailTags.add(tagDetailModel);
                }
            }
            detailModel.setTags(detailTags);
            // 设置是否关注
            for (QuestionStarModel starModel : starModels){
                if(starModel.getQuestionId().equals(detailModel.getId())){
                    detailModel.setUseStar(true);
                    break;
                }
            }
            // 设置回答
            List<QuestionAnswerDetailModel> answerModels = new LinkedList<>();
            for (QuestionAnswerDetailModel answerDetailModel : answerDetailModels){
                if(answerDetailModel.getQuestionId().equals(detailModel.getId())){
                    answerModels.add(answerDetailModel);
                }
            }
            detailModel.setAnswerList(answerModels);
            // 是否是作者
            detailModel.setUseAuthor(detailModel.getCreatedUserId().equals(searchUserId));
        }
        return detailModels;
    }

    @Override
    public int count(HashSet<Long> ids, String mode, Long userId, Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long ip,
                           RewardType rewardType,
                           Double minRewardValue,
                           Double maxRewardValue, HashSet<Long> tags,Integer minComments,Integer maxComments, Integer minAnswers, Integer maxAnswers, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime) {
        if(ids == null) ids = new HashSet<>();
        ids.addAll(formTags(tags));
        return questionInfoDao.count(ids,mode,userId,isDisabled,isAdopted,isPublished,isDeleted,ip,rewardType,minRewardValue,maxRewardValue,minComments,maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime);
    }

    @Override
    public List<QuestionDetailModel> search(HashSet<Long> ids, String mode, Long userId,Long searchUserId,Boolean hasContent,Integer childCount, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(ids,mode,userId,searchUserId,hasContent,null,null,null,null,null,childCount,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<QuestionDetailModel> search(Long searchUserId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished,Integer childCount,
                                            RewardType rewardType,
                                            Double minRewardValue,
                                            Double maxRewardValue, HashSet<Long> tags,Integer minComments,Integer maxComments, Integer minAnswers, Integer maxAnswers, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,null,null,searchUserId,hasContent,isDisabled,isDeleted,isAdopted,isPublished,null,childCount,rewardType,minRewardValue,maxRewardValue,tags,minComments,maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<QuestionDetailModel> search(Long searchUserId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished,Integer childCount,
                                            RewardType rewardType,
                                            Double minRewardValue,
                                            Double maxRewardValue, HashSet<Long> tags,Integer minComments,Integer maxComments, Integer minAnswers, Integer maxAnswers, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(searchUserId,hasContent,isDisabled,isDeleted,isAdopted,isPublished,childCount,rewardType,minRewardValue,maxRewardValue,tags,minComments,maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished,
                           RewardType rewardType,
                           Double minRewardValue,
                           Double maxRewardValue, HashSet<Long> tags,Integer minComments,Integer maxComments, Integer minAnswers, Integer maxAnswers, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime) {
        return count(null,null,null,isDisabled,isDeleted,isAdopted,isPublished,null,rewardType,minRewardValue,maxRewardValue,tags,minComments,maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime);
    }

    @Override
    public List<QuestionDetailModel> search(Long searchUserId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(searchUserId,hasContent,isDisabled,isDeleted,isAdopted,isPublished,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<QuestionDetailModel> search(Long searchUserId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(searchUserId,hasContent,isDisabled,isDeleted,isAdopted,isPublished,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long beginTime, Long endTime) {
        return count(isDisabled,isDeleted,isAdopted,isPublished,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime);
    }

    @Override
    public PageModel<QuestionDetailModel> page(Long searchUserId,Long userId,Boolean hasContent,Boolean isDisabled, Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Integer childCount, RewardType rewardType, Double minRewardValue, Double maxRewardValue,  HashSet<Long> tags,Integer minComments, Integer maxComments, Integer minAnswers, Integer maxAnswers, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order,Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<QuestionDetailModel> data = search(null,null,userId,searchUserId,hasContent,isDisabled,isDeleted,isAdopted,isPublished,null,childCount,rewardType,minRewardValue,maxRewardValue,tags,minComments,maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);
        Integer total = count(null,null,userId,isDisabled,isDeleted,isAdopted,isPublished,null,rewardType,minRewardValue,maxRewardValue,tags,minComments,maxComments,minAnswers,maxAnswers,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public PageModel<QuestionDetailModel> page(Long searchUserId,Long userId,Boolean hasContent,Boolean isDisabled,Boolean isDeleted, Boolean isAdopted, Boolean isPublished, Long beginTime, Long endTime, Integer offset, Integer limit) {
        return page(searchUserId,userId,hasContent,isDisabled,isDeleted,isAdopted,isPublished,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,null,offset,limit);
    }

    @Override
    public QuestionDetailModel getDetail(Long id, String mode, Long userId) {
        List<QuestionDetailModel> listQuestion = search(id != null ? new HashSet<Long>(){{add(id);}} : null,mode,userId,null,null,null,null,null,null,0,1);
        return listQuestion != null && listQuestion.size() > 0 ? listQuestion.get(0) : null;
    }

    @Override
    public QuestionDetailModel getDetail(Long id, String mode) {
        return getDetail(id,mode,null);
    }

    @Override
    public QuestionDetailModel getDetail(Long id, Long userId, Long searchUserId) {
        List<QuestionDetailModel> listQuestion = search(id != null ? new HashSet<Long>(){{add(id);}} : null,null,userId,searchUserId,null,null,null,null,null,0,1);
        return listQuestion != null && listQuestion.size() > 0 ? listQuestion.get(0) : null;
    }

    @Override
    public QuestionDetailModel getDetail(Long id) {
        return getDetail(id,(String) null);
    }

    @Override
    public QuestionDetailModel getDetail(Long id, Long userId) {
        return getDetail(id,(String)null,userId);
    }

}
