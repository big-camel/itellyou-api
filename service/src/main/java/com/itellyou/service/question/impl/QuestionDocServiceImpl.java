package com.itellyou.service.question.impl;

import com.alibaba.fastjson.JSONObject;
import com.itellyou.model.event.QuestionEvent;
import com.itellyou.model.event.TagIndexEvent;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.question.QuestionVersionModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.RewardType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDraftModel;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.*;
import com.itellyou.service.tag.TagInfoService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.RedisUtils;
import com.itellyou.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class QuestionDocServiceImpl implements QuestionDocService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionSearchService searchService;
    private final QuestionSingleService singleService;
    private final QuestionInfoService infoService;
    private final QuestionVersionService versionService;
    private final QuestionTagService questionTagService;
    private final TagInfoService tagService;
    private final UserDraftService draftService;
    private final UserInfoService userService;
    private final OperationalPublisher operationalPublisher;

    public QuestionDocServiceImpl(QuestionSearchService searchService, QuestionSingleService singleService, QuestionInfoService infoService, QuestionVersionService versionService, QuestionTagService questionTagService, TagInfoService tagService, UserDraftService draftService, UserInfoService userService, OperationalPublisher operationalPublisher) {
        this.searchService = searchService;
        this.singleService = singleService;
        this.infoService = infoService;
        this.versionService = versionService;
        this.questionTagService = questionTagService;
        this.tagService = tagService;
        this.draftService = draftService;
        this.userService = userService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    public Long create(Long userId, String title, String content, String html, String description, RewardType rewardType, Double rewardValue, Double rewardAdd, HashSet<Long> tagIds, String remark, String save_type, Long ip) throws Exception {
        try{
            QuestionInfoModel infoModel = new QuestionInfoModel();
            infoModel.setDraft(0);
            infoModel.setCreatedIp(ip);
            infoModel.setCreatedTime(DateUtils.getTimestamp());
            infoModel.setCreatedUserId(userId);
            int resultRows = infoService.insert(infoModel);
            if(resultRows != 1)
                throw new Exception("写入提问失败");
            QuestionVersionModel versionModel = addVersion(infoModel.getId(),userId,title,content,html,description,rewardType,rewardValue,rewardAdd,tagIds,remark,1,save_type,ip,false,true);
            if(versionModel == null)
                throw new Exception("写入版本失败");
            RedisUtils.removeCache("question",infoModel.getId());
            return infoModel.getId();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    private HashSet<Long> findNotExistTag(HashSet<Long> source,HashSet<Long> target){
        HashSet<Long> list = new LinkedHashSet<>();
        for(Long sourceTag:source){
            boolean exist = false;
            if(target != null){
                for(Long targetTag:target){
                    if(sourceTag.equals(targetTag)){
                        exist = true;
                        break;
                    }
                }
            }
            if(!exist){
                list.add(sourceTag);
            }
        }
        return list;
    }

    @Override
    @Transactional
    @CacheEvict(value = "question",key = "#id")
    public QuestionVersionModel addVersion(Long id, Long userId, String title, String content, String html, String description, RewardType rewardType, Double rewardValue, Double rewardAdd, HashSet<Long> tagIds, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception {
        try {
            QuestionVersionModel versionModel = new QuestionVersionModel();
            versionModel.setQuestionId(id);
            QuestionDetailModel detailModel = searchService.getDetail(id, "draft",userId);
            if(detailModel == null)
            {
                QuestionInfoModel infoModel = singleService.findById(id);
                if(infoModel != null){
                    detailModel = new QuestionDetailModel();
                    detailModel.setId(infoModel.getId());
                    detailModel.setCreatedUserId(infoModel.getCreatedUserId());
                }
            }
            if (force) {
                versionModel.setTitle(title);
                versionModel.setContent(content);
                versionModel.setHtml(html);
            } else {
                if (StringUtils.isNotEmpty(title) && !title.equals(detailModel.getTitle())) {
                    versionModel.setTitle(title);
                }
                if (StringUtils.isNotEmpty(content)&& !content.equals(detailModel.getContent())) {
                    versionModel.setContent(content);
                    versionModel.setHtml(html);
                    if(!StringUtils.isNotEmpty(versionModel.getTitle())){
                        versionModel.setTitle(detailModel.getTitle());
                    }
                }else if(StringUtils.isNotEmpty(versionModel.getTitle())){
                    versionModel.setContent(detailModel.getContent());
                    versionModel.setHtml(detailModel.getHtml());
                }
            }
            if(detailModel == null) return null;
            HashSet<Long> oldTags = new HashSet<>();
            for (TagDetailModel tagDetail : detailModel.getTags()){
                oldTags.add(tagDetail.getId());
            }

            // 查找新添加的标签
            HashSet<Long> addTags = tagIds != null ? findNotExistTag(tagIds,oldTags) : new HashSet<>();
            // 查找需要删除的标签
            HashSet<Long> delTags = tagIds != null ? findNotExistTag(oldTags,tagIds) : new HashSet<>();
            // 当有新内容更新时才需要新增版本
            List<TagDetailModel> tagDetailModels = new ArrayList<>();
            if (tagIds != null) {
                for (Long tagId : tagIds){
                    TagDetailModel tagDetailModel = new TagDetailModel();
                    tagDetailModel.setId(tagId);
                    tagDetailModels.add(tagDetailModel);
                }
            }
            if (StringUtils.isNotEmpty(versionModel.getTitle())|| StringUtils.isNotEmpty(versionModel.getContent())) {
                if(isPublish == true){
                    versionModel.setRewardType(rewardType == null ? detailModel.getRewardType() : rewardType);
                    versionModel.setRewardValue(rewardValue == null ? detailModel.getRewardValue() : rewardValue);
                    versionModel.setDescription(description);
                    versionModel.setRewardAdd(rewardAdd == null ? 0.0 : rewardAdd);
                    versionModel.setTags(tagDetailModels);

                    if(addTags != null && addTags.size() > 0){
                        int result = tagService.updateQuestionCountById(addTags,1);
                        if(result != addTags.size()) throw new Exception("更新标签提问数量失败");
                    }
                    if(delTags != null && delTags.size() > 0){
                        int result = tagService.updateQuestionCountById(delTags,-1);
                        if(result != delTags.size()) throw new Exception("更新标签提问数量失败");
                    }
                    if(StringUtils.isEmpty(detailModel.getCover())){
                        JSONObject coverObj = StringUtils.getEditorContentCover(content);
                        if(coverObj != null){
                            int result = infoService.updateMetas(id,coverObj.getString("src"));
                            if(result != 1) throw new Exception("更新封面失败");
                        }
                    }
                    // 更新冗余信息
                    infoService.updateInfo(id,versionModel.getTitle(),versionModel.getDescription(),versionModel.getRewardType(),versionModel.getRewardAdd(),versionModel.getRewardValue(),DateUtils.getTimestamp(),ip,userId);
                    // 更新冗余标签
                    questionTagService.clear(id);
                    if(tagIds.size() > 0)
                        questionTagService.addAll(id,tagIds);
                }else{
                    versionModel.setDescription(description == null ? detailModel.getDescription() : description);
                    versionModel.setRewardType(rewardType == null ? detailModel.getRewardType() : rewardType);
                    versionModel.setRewardValue(rewardValue == null ? detailModel.getRewardValue() : rewardValue);
                    versionModel.setRewardAdd(rewardAdd == null ? 0.0 : rewardAdd);
                    versionModel.setTags(tagIds == null ?  detailModel.getTags() : tagDetailModels);
                }

                versionModel.setPublished(isPublish);
                versionModel.setVersion(version);
                versionModel.setRemark(remark);
                versionModel.setSaveType(save_type);
                versionModel.setCreatedTime(DateUtils.getTimestamp());
                versionModel.setCreatedUserId(userId);
                versionModel.setCreatedIp(ip);

                int rows = isPublish == true ? versionService.updateVersion(versionModel) : versionService.updateDraft(versionModel);
                if (rows != 1)
                    throw new Exception("新增版本失败");
                //第一次发布，更新用户的问题数量
                if(isPublish == true && detailModel.isPublished() == false){
                    int result = userService.updateQuestionCount(detailModel.getCreatedUserId(),1);
                    if(result != 1) throw new Exception("更新用户问题数量失败");
                }
            }
            String url = "/question/" + id.toString();
            String draftTitle = versionModel.getTitle();
            if(StringUtils.isEmpty(draftTitle)) draftTitle = "无标题";
            UserDraftModel draftModel = new UserDraftModel(detailModel.getCreatedUserId(),url,draftTitle,StringUtils.getFragmenter(versionModel.getContent()), EntityType.QUESTION,id,DateUtils.getTimestamp(),ip,userId);
            draftService.insertOrUpdate(draftModel);

            if(isPublish){
                if(!detailModel.isPublished()){
                    operationalPublisher.publish(new QuestionEvent(this, EntityAction.PUBLISH,
                            detailModel.getId(),detailModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
                }else{
                    operationalPublisher.publish(new QuestionEvent(this, EntityAction.UPDATE,
                            detailModel.getId(),detailModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
                }
                HashSet<Long> publishTagIds = new HashSet<>();
                if(addTags != null && addTags.size() > 0){
                    for(Long i : addTags){
                        if(!publishTagIds.contains(i)) publishTagIds.add(i);
                    }
                }
                if(delTags != null && delTags.size() > 0){
                    for(Long i : delTags){
                        if(!publishTagIds.contains(i)) publishTagIds.add(i);
                    }
                }
                if(publishTagIds.size() > 0){
                    operationalPublisher.publish(new TagIndexEvent(this,publishTagIds));
                }
            }
            return versionModel;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }
}
