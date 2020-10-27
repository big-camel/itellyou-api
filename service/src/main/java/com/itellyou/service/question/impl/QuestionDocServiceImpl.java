package com.itellyou.service.question.impl;

import com.alibaba.fastjson.JSONObject;
import com.itellyou.dao.question.QuestionInfoDao;
import com.itellyou.model.constant.CacheKeys;
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
import com.itellyou.service.tag.TagSingleService;
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

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

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
    private final TagSingleService tagSingleService;
    private final QuestionVersionTagService versionTagService;
    private final QuestionInfoDao infoDao;

    public QuestionDocServiceImpl(QuestionSearchService searchService, QuestionSingleService singleService, QuestionInfoService infoService, QuestionVersionService versionService, QuestionTagService questionTagService, TagInfoService tagService, UserDraftService draftService, UserInfoService userService, OperationalPublisher operationalPublisher, TagSingleService tagSingleService, QuestionVersionTagService versionTagService, QuestionInfoDao infoDao) {
        this.searchService = searchService;
        this.singleService = singleService;
        this.infoService = infoService;
        this.versionService = versionService;
        this.questionTagService = questionTagService;
        this.tagService = tagService;
        this.draftService = draftService;
        this.userService = userService;
        this.operationalPublisher = operationalPublisher;
        this.tagSingleService = tagSingleService;
        this.versionTagService = versionTagService;
        this.infoDao = infoDao;
    }

    @Override
    @Transactional
    public Long create(Long userId, String title, String content, String html, String description, RewardType rewardType, Double rewardValue, Double rewardAdd, Collection<Long> tagIds, String remark, String save_type, Long ip) throws Exception {
        try{
            QuestionInfoModel infoModel = new QuestionInfoModel();
            infoModel.setDraft(0);
            infoModel.setCreatedIp(ip);
            infoModel.setCreatedTime(DateUtils.toLocalDateTime());
            infoModel.setCreatedUserId(userId);
            int resultRows = infoService.insert(infoModel);
            if(resultRows != 1)
                throw new Exception("写入提问失败");
            RedisUtils.set(CacheKeys.QUESTION_KEY,infoModel.getId(),infoModel);
            QuestionVersionModel versionModel = addVersion(infoModel.getId(),userId,title,content,html,description,rewardType,rewardValue,rewardAdd,tagIds,remark,1,save_type,ip,false,true);
            if(versionModel == null)
                throw new Exception("写入版本失败");
            RedisUtils.remove(CacheKeys.QUESTION_KEY,infoModel.getId());
            return infoModel.getId();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheKeys.QUESTION_KEY,key = "#id")
    public QuestionVersionModel addVersion(Long id, Long userId, String title, String content, String html, String description, RewardType rewardType, Double rewardValue, Double rewardAdd, Collection<Long> tagIds, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception {
        try {
            QuestionVersionModel versionModel = new QuestionVersionModel();
            versionModel.setQuestionId(id);
            QuestionDetailModel detailModel = searchService.getDetail(id, "draft",userId);
            // 刚创建，没有任何版本信息
            if(detailModel == null)
            {
                // 获取文章基本信息
                QuestionInfoModel infoModel = singleService.findById(id);
                if(infoModel != null){
                    // 实例化一个详细信息的文章
                    detailModel = new QuestionDetailModel();
                    detailModel.setId(infoModel.getId());
                    detailModel.setCreatedUserId(infoModel.getCreatedUserId());
                }else throw new Exception("问题不存在");
            }
            //初始化原版本内容
            versionModel.setTitle(detailModel.getTitle());
            versionModel.setContent(detailModel.getContent());
            versionModel.setHtml(detailModel.getHtml());
            versionModel.setDescription(detailModel.getDescription());
            versionModel.setRewardType(detailModel.getRewardType());
            versionModel.setRewardAdd(detailModel.getRewardAdd());
            versionModel.setRewardValue(detailModel.getRewardValue());
            //判断是否需要新增版本
            boolean isAdd = force;
            // 强制更新
            if (force) {
                versionModel.setTitle(title);
                versionModel.setContent(content);
                versionModel.setHtml(html);
            } else {
                // 判断标题是否更改
                if (StringUtils.isNotEmpty(title) && !title.equals(versionModel.getTitle())) {
                    versionModel.setTitle(title);
                    isAdd = true;
                }
                // 如果内容有更改，设置新的内容
                if (StringUtils.isNotEmpty(content)&& !content.equals(versionModel.getContent())) {
                    versionModel.setContent(content);
                    versionModel.setHtml(html);
                    isAdd = true;
                }
            }

            Collection<Long> oldTags = new HashSet<>();
            for (TagDetailModel tagDetail : detailModel.getTags()){
                oldTags.add(tagDetail.getId());
            }

            Collection<Long> oldTagIds = detailModel.getTags().stream().map(TagDetailModel::getId).collect(Collectors.toSet());
            // 查找新添加的标签
            Collection<Long> addTags = tagIds != null ? tagSingleService.findNotExist(tagIds,oldTagIds) : new HashSet<>();
            // 查找需要删除的标签
            Collection<Long> delTags = tagIds != null ? tagSingleService.findNotExist(oldTagIds,tagIds) : new HashSet<>();

            if (isAdd) {
                if(isPublish == true){
                    if(rewardType != null) versionModel.setRewardType(rewardType);
                    if(rewardValue != null) versionModel.setRewardValue(rewardValue);
                    versionModel.setDescription(description);
                    versionModel.setRewardAdd(rewardAdd == null ? 0.0 : rewardAdd);

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
                    if(description != null)versionModel.setDescription(description);
                    if(rewardType != null)versionModel.setRewardType(rewardType);
                    if(rewardValue != null)versionModel.setRewardValue(rewardValue);
                    versionModel.setRewardAdd(rewardAdd == null ? 0.0 : rewardAdd);
                }
                versionModel.setPublished(isPublish);
                versionModel.setVersion(version);
                versionModel.setRemark(remark);
                versionModel.setSaveType(save_type);
                versionModel.setCreatedTime(DateUtils.toLocalDateTime());
                versionModel.setCreatedUserId(userId);
                versionModel.setCreatedIp(ip);

                int result = versionService.insert(versionModel);
                if(result < 1) throw new Exception("写入版本失败");
                result = infoDao.updateVersion(id,isPublish ? versionModel.getVersion() : null,versionModel.getVersion(),isPublish && !detailModel.isPublished() ? true : null,DateUtils.getTimestamp(),ip,userId);
                if(result != 1) throw new Exception("更新版本失败");
                //增加版本标签，如果没有设置，继承原来的
                versionTagService.addAll(versionModel.getId(),tagIds == null ? oldTagIds : tagIds);
                //第一次发布，更新用户的问题数量
                if(isPublish == true && detailModel.isPublished() == false) {
                    result = userService.updateQuestionCount(detailModel.getCreatedUserId(),1);
                    if(result != 1) throw new Exception("更新用户问题数量失败");
                }
            }
            String url = "/question/" + id.toString();
            String draftTitle = versionModel.getTitle();
            if(StringUtils.isEmpty(draftTitle)) draftTitle = "无标题";
            UserDraftModel draftModel = new UserDraftModel(detailModel.getCreatedUserId(),url,draftTitle,StringUtils.getFragmenter(versionModel.getContent()), EntityType.QUESTION,id,DateUtils.toLocalDateTime(),ip,userId);
            draftService.insertOrUpdate(draftModel);

            if(isPublish){
                if(!detailModel.isPublished()){
                    operationalPublisher.publish(new QuestionEvent(this, EntityAction.PUBLISH,
                            detailModel.getId(),detailModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));
                }else{
                    operationalPublisher.publish(new QuestionEvent(this, EntityAction.UPDATE,
                            detailModel.getId(),detailModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));
                }
                Collection<Long> publishTagIds = new HashSet<>();
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
