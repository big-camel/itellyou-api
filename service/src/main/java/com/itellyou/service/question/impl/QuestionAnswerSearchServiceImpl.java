package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.*;
import com.itellyou.model.sys.*;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.question.*;
import com.itellyou.service.sys.EntitySearchService;
import com.itellyou.service.sys.EntityService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

@Service
@CacheConfig(cacheNames = CacheKeys.QUESTION_ANSWER_KEY)
public class QuestionAnswerSearchServiceImpl implements QuestionAnswerSearchService , EntitySearchService<QuestionAnswerDetailModel> {

    private final QuestionAnswerDao answerDao;
    private final UserSearchService userSearchService;
    private final QuestionAnswerPaidReadSearchService paidReadSearchService;
    private final QuestionAnswerVersionSearchService versionSearchService;
    private final QuestionAnswerStarSingleService starSingleService;
    private final QuestionAnswerVoteService answerVoteService;
    private final QuestionAnswerSingleService singleService;
    private final QuestionSearchService questionSearchService;
    private final EntityService entityService;
    private final QuestionAnswerVersionSingleService versionSingleService;

    @Autowired
    public QuestionAnswerSearchServiceImpl(QuestionAnswerDao answerDao, UserSearchService userSearchService, QuestionAnswerPaidReadSearchService paidReadSearchService, QuestionAnswerVersionSearchService versionSearchService, QuestionAnswerStarSingleService starSingleService, QuestionAnswerVoteService answerVoteService, QuestionAnswerSingleService singleService, @Lazy QuestionSearchService questionSearchService, EntityService entityService, QuestionAnswerVersionSingleService versionSingleService){
        this.answerDao = answerDao;
        this.userSearchService = userSearchService;
        this.paidReadSearchService = paidReadSearchService;
        this.versionSearchService = versionSearchService;
        this.starSingleService = starSingleService;
        this.answerVoteService = answerVoteService;
        this.singleService = singleService;
        this.questionSearchService = questionSearchService;
        this.entityService = entityService;
        this.versionSingleService = versionSingleService;
    }

    @Override
    public List<QuestionAnswerDetailModel> search(Collection<Long> ids, Collection<Long> questionIds, String mode, Long searchUserId, Long userId, Boolean hasContent, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted,Long ip, Integer minComment, Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionAnswerModel> infoModels = singleService.search(ids,questionIds,mode,userId,isAdopted,isDisabled,isPublished,isDeleted,ip,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);

        List<QuestionAnswerDetailModel> detailModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;
        Collection<Long> authorIds = new LinkedHashSet<>();
        Collection<Long> fetchIds = new LinkedHashSet<>();
        Collection<Long> fetchQuestionIds = new LinkedHashSet<>();
        HashMap<Long,Integer> versionMap = new LinkedHashMap<>();
        List<QuestionAnswerVersionModel> versionModels = new LinkedList<>();
        List<QuestionAnswerPaidReadModel> paidReadModels = new LinkedList<>();

        for (QuestionAnswerModel infoModel : infoModels){
            QuestionAnswerDetailModel detailModel = new QuestionAnswerDetailModel(infoModel);
            // 获取内容
            if(hasContent == null || hasContent == true  || mode == "draft"){
                versionMap.put(infoModel.getId(),!"draft".equals(mode) ? infoModel.getVersion() : infoModel.getDraft());
            }
            fetchIds.add(infoModel.getId());
            if(infoModel.getQuestionId() != null && !fetchQuestionIds.contains(infoModel.getQuestionId()))
                fetchQuestionIds.add(infoModel.getQuestionId());

            if(searchUserId != null){
                boolean isEquals = infoModel.getCreatedUserId().equals(searchUserId);
                detailModel.setAllowDelete(isEquals && !infoModel.isAdopted());
                detailModel.setAllowEdit(isEquals && !infoModel.isDeleted());
                detailModel.setAllowOppose(!isEquals);
                detailModel.setAllowStar(!isEquals);
                detailModel.setAllowSupport(!isEquals);
            }
            if(!authorIds.contains(infoModel.getCreatedUserId())) authorIds.add(infoModel.getCreatedUserId());
            detailModels.add(detailModel);
        }
        // 一次查出需要的作者
        List<UserDetailModel> userDetailModels = authorIds.size() > 0 ? userSearchService.search(authorIds,null,null,null,null,null,null,null,null,null,null,null) : new ArrayList<>();
        // 一次查出需要的问题
        List<QuestionDetailModel> questionDetailModels = questionSearchService.search(fetchQuestionIds,null,null,searchUserId,false,null,null,null,null,null,null);
        // 一次查出需要的付费设置
        paidReadModels = paidReadSearchService.search(fetchIds);
        // 一次查出需要的版本信息
        versionModels = versionMap.size() > 0 ? versionSingleService.searchByAnswerMap(versionMap,hasContent) : new ArrayList<>();
        // 一次查出是否有收藏
        List<QuestionAnswerStarModel> starModels = new ArrayList<>();
        List<QuestionAnswerVoteModel> voteModels = new ArrayList<>();
        if(searchUserId != null){
            starModels = starSingleService.search(fetchIds,searchUserId);
            voteModels = answerVoteService.search(fetchIds,searchUserId);
        }
        for (QuestionAnswerDetailModel detailModel : detailModels){
            // 设置问题
            for (QuestionDetailModel questionDetailModel : questionDetailModels){
                if(questionDetailModel.getId().equals(detailModel.getQuestionId())){
                    detailModel.setQuestion(questionDetailModel);
                    detailModel.setUseAuthor(questionDetailModel.getCreatedUserId().equals(detailModel.getCreatedUserId()));
                    if(questionDetailModel.getCreatedUserId().equals(searchUserId) && !questionDetailModel.isAdopted() && !detailModel.isAdopted()){
                        detailModel.setAllowAdopt(true);
                    }
                    break;
                }
            }
            // 设置版本信息
            for(QuestionAnswerVersionModel versionModel : versionModels){
                if(versionModel.getAnswerId().equals(detailModel.getId())) {
                    detailModel.setHtml(versionModel.getHtml());
                    detailModel.setContent(versionModel.getContent());
                    if (mode == "draft") {
                        detailModel.setDescription(versionModel.getDescription());
                        detailModel.setUpdatedIp(versionModel.getCreatedIp());
                        detailModel.setUpdatedTime(versionModel.getCreatedTime());
                        detailModel.setUpdatedUserId(versionModel.getCreatedUserId());
                    }
                    break;
                }
            }
            // 付费设置
            for(QuestionAnswerPaidReadModel paidReadModel : paidReadModels) {
                if (paidReadModel.getAnswerId().equals(detailModel.getId())) {
                    detailModel.setPaidRead(paidReadModel);
                    if(mode != "draft" && paidReadSearchService.checkRead(paidReadModel,detailModel.getQuestionId(),detailModel.getCreatedUserId(),searchUserId) == false){
                        String content =  HtmlUtils.subEditorContent(detailModel.getContent(),detailModel.getHtml(),paidReadModel.getFreeReadScale());
                        detailModel.setContent(content);
                        String description;
                        String html = detailModel.getHtml();
                        if(hasContent != null && hasContent == false){
                            QuestionAnswerVersionModel versionModel = versionSingleService.find(detailModel.getId(),detailModel.getVersion());
                            if(versionModel != null) html = versionModel.getHtml();
                        }

                        String text = StringUtils.removeHtmlTags(html);
                        int len = new BigDecimal(text.length()).multiply(new BigDecimal(paidReadModel.getFreeReadScale())).intValue();
                        if(len >= text.length()) len = text.length() - 1;
                        if(len <= 0) description = "";
                        else {
                            description = text.substring(0, len);
                            description = StringUtils.getFragmenter(description);
                        }
                        detailModel.setDescription(description);

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
            // 设置是否收藏
            for (QuestionAnswerStarModel starModel : starModels){
                if(starModel.getAnswerId().equals(detailModel.getId())){
                    detailModel.setUseStar(true);
                    break;
                }
            }
            // 获取是否点赞
            for(QuestionAnswerVoteModel voteModel : voteModels){
                if(voteModel.getAnswerId().equals(detailModel.getId())){
                    detailModel.setUseOppose(VoteType.OPPOSE.equals(voteModel.getType()));
                    detailModel.setUseSupport(VoteType.SUPPORT.equals(voteModel.getType()));
                }
            }
        }
        return detailModels;
    }

    @Override
    public List<QuestionAnswerDetailModel> search(Collection<Long> ids, Collection<Long> questionIds, String mode, Long searchUserId, Long userId, Boolean hasContent,Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(ids,questionIds,mode,searchUserId,userId,hasContent,isAdopted,isDisabled,isPublished,isDeleted,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<QuestionAnswerDetailModel> search(Collection<Long> ids, Collection<Long> questionIds, String mode, Long searchUserId, Long userId,Boolean hasContent, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(ids,questionIds,mode,searchUserId,userId,hasContent,null,null,null,null,beginTime,endTime,order,offset,limit);

    }

    @Override
    public List<QuestionAnswerDetailModel> search(Long questionId, Long searchUserId,Boolean hasContent,Boolean isAdopted, Boolean isDisabled, Boolean isPublished,Boolean isDeleted, Integer minComment, Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(null,questionId != null ? new HashSet<Long>(){{add(questionId);}} : null,null,searchUserId,null,hasContent,isAdopted,isDisabled,isPublished,isDeleted,null,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);

    }

    @Override
    public List<QuestionAnswerDetailModel> search(Long questionId, Long searchUserId,Boolean hasContent,Boolean isAdopted, Boolean isDisabled,Boolean isDeleted, Boolean isPublished, Integer minComment, Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(questionId,searchUserId,hasContent,isAdopted,isDisabled,isPublished,isDeleted,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Long questionId,Boolean isAdopted, Boolean isDisabled, Boolean isPublished,Boolean isDeleted, Integer minComment, Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime) {
        return singleService.count(null,questionId != null ? new HashSet<Long>(){{add(questionId);}} : null,null,null,isAdopted,isDisabled,isPublished,isDeleted,null,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime);
    }

    @Override
    public List<QuestionAnswerDetailModel> search(Long questionId, Long searchUserId,Boolean hasContent,Boolean isAdopted, Boolean isDisabled, Boolean isPublished,Boolean isDeleted, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return search(questionId,searchUserId,hasContent,isAdopted,isDisabled,isPublished,isDeleted,null,null,null,null,null,null,null,null,null,null,beginTime,endTime,order,offset,limit);

    }

    @Override
    public List<QuestionAnswerDetailModel> search(Long questionId, Long searchUserId,Boolean hasContent,Boolean isAdopted, Boolean isDisabled, Boolean isPublished,Boolean isDeleted, Long beginTime, Long endTime, Integer offset, Integer limit) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return search(questionId,searchUserId,hasContent,isAdopted,isDisabled,isPublished,isDeleted,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Long questionId,Boolean isAdopted, Boolean isDisabled, Boolean isPublished,Boolean isDeleted, Long beginTime, Long endTime) {
        return count(questionId,isAdopted,isDisabled,isPublished,isDeleted,null,null,null,null,null,null,null,null,null,null,beginTime,endTime);

    }

    @Override
    public PageModel<QuestionAnswerDetailModel> page(Long questionId, Long searchUserId,Long userId,Boolean hasContent,Boolean isAdopted, Boolean isDisabled, Boolean isPublished,Boolean isDeleted, Long beginTime, Long endTime,Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<QuestionAnswerDetailModel> data = search(null,questionId != null ? new HashSet<Long>(){{add(questionId);}} : null,null,searchUserId,userId,hasContent,isAdopted,isDisabled,isPublished,isDeleted,beginTime,endTime,order,offset,limit);
        Integer total = singleService.count(null,questionId != null ? new HashSet<Long>(){{add(questionId);}} : null,null,userId,isAdopted,isDisabled,isPublished,isDeleted,null,null,null,null,null,null,null,null,null,null,null,beginTime,endTime);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public PageModel<QuestionAnswerDetailModel> page(Collection<Long> ids, Collection<Long> questionIds, String mode, Long searchUserId, Long userId, Boolean hasContent, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long ip, Integer minComment, Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<QuestionAnswerDetailModel> data = search(ids,questionIds,mode,searchUserId,userId,hasContent,isAdopted,isDisabled,isPublished,isDeleted,ip,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order,offset,limit);
        Integer total = singleService.count(ids,questionIds,mode,userId,isAdopted,isDisabled,isPublished,isDeleted,ip,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public List<QuestionAnswerModel> searchChild(Collection<Long> ids, Collection<Long> questionIds, String mode, Long userId, Integer childCount, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long ip, Integer minComment, Integer maxComment, Integer minView, Integer maxView, Integer minSupport, Integer maxSupport, Integer minOppose, Integer maxOppose, Integer minStar, Integer maxStar, Long beginTime, Long endTime, Map<String, String> order) {
        return RedisUtils.fetch(CacheKeys.QUESTION_ANSWER_KEY,QuestionAnswerModel.class,ids,(Collection<Long> fetchIds) ->
                answerDao.searchChild(fetchIds,questionIds,mode,userId,childCount,isAdopted,isDisabled,isPublished,isDeleted,ip,minComment,maxComment,minView,maxView,minSupport,maxSupport,minOppose,maxOppose,minStar,maxStar,beginTime,endTime,order)
        );
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id, Long questionId, String mode, Long searchUserId, Long userId, Boolean hasContent,Boolean isAdopted, Boolean isDisabled,Boolean isPublished, Boolean isDeleted) {
        List<QuestionAnswerDetailModel> listQuestion = search(id != null ? new HashSet<Long>(){{add(id);}} : null,questionId != null ? new HashSet<Long>(){{add(questionId);}} : null,mode,searchUserId,userId,hasContent,isAdopted,isDisabled,isPublished,isDeleted,null,null,null,0,1);
        return listQuestion != null && listQuestion.size() > 0 ? listQuestion.get(0) : null;
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id,Long questionId, String mode, Long searchUserId, Long userId,Boolean hasContent) {
        List<QuestionAnswerDetailModel> listQuestion = search(id != null ? new HashSet<Long>(){{add(id);}} : null,questionId != null ? new HashSet<Long>(){{add(questionId);}} : null,mode,searchUserId,userId,hasContent,null,null,null,0,1);
        return listQuestion != null && listQuestion.size() > 0 ? listQuestion.get(0) : null;
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id, Long questionId, String mode, Long searchUserId, Long userId) {
        return getDetail(id,questionId,mode,null,userId,null);
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id,Long questionId, String mode,Boolean hasContent) {
        return getDetail(id,questionId,mode,null,null,hasContent);
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id,Long questionId, Long searchUserId, Long userId,Boolean hasContent) {
        return getDetail(id,questionId,null,searchUserId,userId,hasContent);
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id, Long questionId, Boolean hasContent) {
        return getDetail(id,questionId,null, null,hasContent);
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id,Long questionId) {
        return getDetail(id,questionId,(Boolean) null);
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id, Boolean hasContent) {
        return getDetail(id,null,null, null,hasContent);
    }

    @Override
    public QuestionAnswerDetailModel getDetail(Long id) {
        return getDetail(id,(Boolean) null);
    }

    @Override
    public List<QuestionAnswerTotalDetailModel> totalByUser(Collection<Long> userIds,Long searchUserId, Long questionId, Boolean isAdopted, Boolean isDisabled, Boolean isPublished, Boolean isDeleted, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<QuestionAnswerTotalModel> infoModels = singleService.totalByUser(userIds,questionId,isAdopted,isDisabled,isPublished,isDeleted,beginTime,endTime,order,offset,limit);
        List<QuestionAnswerTotalDetailModel> detailModels = new LinkedList<>();
        if(infoModels.size() == 0) return detailModels;
        EntityDataModel<CacheEntity> entityDataModel = entityService.search(infoModels,(QuestionAnswerTotalModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
            Map<String,Object> args = getArgs.apply(EntityType.USER);
            Collection authorIds = (Collection)args.computeIfAbsent("ids",key -> new HashSet<>());
            if(!authorIds.contains(model.getUserId())) authorIds.add(model.getUserId());
            args.put("ids",authorIds);
            args.put("searchUserId",searchUserId);
            return new EntitySearchModel(EntityType.USER,args);
        });
        for(QuestionAnswerTotalModel totalModel : infoModels){
            QuestionAnswerTotalDetailModel detailModel = new QuestionAnswerTotalDetailModel(totalModel);
            // 获取作者
            detailModel.setUser(entityDataModel.get(EntityType.USER,totalModel.getUserId()));
            detailModels.add(detailModel);
        }
        return detailModels;
    }

    @Override
    public List<QuestionAnswerDetailModel> search(Map<String, Object> args) {
        Params params = new Params(args);
        return search(params.get("ids",Collection.class),
                params.get("questionIds",Collection.class),
                params.get("mode",String.class),
                params.get("searchUserId",Long.class),
                params.get("userId",Long.class),
                params.get("hasContent",Boolean.class),
                params.get("isAdopted",Boolean.class),
                params.get("isDisabled",Boolean.class),
                params.get("isPublished",Boolean.class),
                params.get("isDeleted",Boolean.class),
                params.get("ip",Long.class),
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
                params.get("beginTime",Long.class),
                params.get("endTime",Long.class),
                params.get("order",Map.class),
                params.get("offset",Integer.class),
                params.get("limit",Integer.class));
    }
}
