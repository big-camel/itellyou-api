package com.itellyou.service.question.impl;

import com.alibaba.fastjson.JSONObject;
import com.itellyou.dao.question.QuestionAnswerDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.event.AnswerEvent;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerVersionModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserDraftModel;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.*;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.RedisUtils;
import com.itellyou.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = CacheKeys.QUESTION_ANSWER_KEY)
@Service
public class QuestionAnswerDocServiceImpl implements QuestionAnswerDocService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionAnswerDao answerDao;
    private final QuestionAnswerService answerService;
    private final QuestionSearchService questionSearchService;
    private final QuestionInfoService questionInfoService;
    private final QuestionAnswerSingleService answerSingleService;
    private final QuestionAnswerSearchService answerSearchService;
    private final QuestionAnswerVersionService answerVersionService;
    private final UserInfoService userInfoService;
    private final UserDraftService userDraftService;
    private final OperationalPublisher operationalPublisher;

    public QuestionAnswerDocServiceImpl(QuestionAnswerDao answerDao, QuestionAnswerService answerService, QuestionSearchService questionSearchService, QuestionInfoService questionInfoService, QuestionAnswerSingleService answerSingleService, QuestionAnswerSearchService answerSearchService, QuestionAnswerVersionService answerVersionService, UserInfoService userInfoService, UserDraftService userDraftService, OperationalPublisher operationalPublisher) {
        this.answerDao = answerDao;
        this.answerService = answerService;
        this.questionSearchService = questionSearchService;
        this.questionInfoService = questionInfoService;
        this.answerSingleService = answerSingleService;
        this.answerSearchService = answerSearchService;
        this.answerVersionService = answerVersionService;
        this.userInfoService = userInfoService;
        this.userDraftService = userDraftService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheKeys.QUESTION_KEY , key = "#questionId")
    public Long create(Long questionId, Long userId, String content, String html,String description, String remark, String save_type, Long ip) throws Exception {

        try{
            QuestionAnswerModel answerModel = new QuestionAnswerModel();
            answerModel.setQuestionId(questionId);
            answerModel.setDraft(0);
            answerModel.setCreatedIp(ip);
            answerModel.setCreatedTime(DateUtils.toLocalDateTime());
            answerModel.setCreatedUserId(userId);
            int resultRows = answerService.insert(answerModel);
            if(resultRows != 1)
                throw new Exception("写入回答失败");
            RedisUtils.set(CacheKeys.QUESTION_ANSWER_KEY,answerModel.getId(),answerModel);
            QuestionAnswerVersionModel versionModel = addVersion(answerModel.getId(),questionId,userId,content,html,description,remark,1,save_type,ip,false,true);
            if(versionModel == null)
                throw new Exception("写入版本失败");
            RedisUtils.remove(CacheKeys.QUESTION_ANSWER_KEY,answerModel.getId());
            return answerModel.getId();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public QuestionAnswerVersionModel addVersion(Long id, Long questionId, Long userId, String content, String html, String description, String remark, Integer version, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception {
        try {
            QuestionDetailModel questionModel = questionSearchService.getDetail(questionId);
            if(questionModel == null) throw new Exception("错误的提问");
            QuestionAnswerVersionModel versionModel = new QuestionAnswerVersionModel();
            versionModel.setAnswerId(id);
            QuestionAnswerDetailModel detailModel = answerSearchService.getDetail(id, questionId, "draft", null,null);
            // 刚创建，没有任何版本信息
            if(detailModel == null)
            {
                // 获取基本信息
                QuestionAnswerModel infoModel = answerSingleService.findById(id);
                if(infoModel != null){
                    // 实例化详细信息
                    detailModel = new QuestionAnswerDetailModel();
                    detailModel.setId(infoModel.getId());
                    detailModel.setQuestionId(infoModel.getQuestionId());
                    detailModel.setCreatedUserId(infoModel.getCreatedUserId());
                }else throw new Exception("回答不存在");
            }
            //初始化原版本内容
            versionModel.setContent(detailModel.getContent());
            versionModel.setHtml(detailModel.getHtml());
            versionModel.setDescription(detailModel.getDescription());
            //判断是否需要新增版本
            boolean isAdd = force;
            if (force) {
                versionModel.setContent(content);
            } else {
                // 如果内容有更改，设置新的内容
                if (StringUtils.isNotEmpty(content)&& !content.equals(versionModel.getContent())) {
                    versionModel.setContent(content);
                    versionModel.setHtml(html);
                    isAdd = true;
                }
            }
            // 当有新内容更新时才需要新增版本
            if (isAdd) {

                if(isPublish == true && !detailModel.isPublished()){
                    int result = userInfoService.updateAnswerCount(detailModel.getCreatedUserId(),1);
                    if(result != 1) throw new Exception("更新用户回答数量失败");
                }

                if(isPublish && StringUtils.isEmpty(detailModel.getCover())){
                    JSONObject coverObj = StringUtils.getEditorContentCover(content);
                    if(coverObj != null){
                        int result = answerService.updateMetas(id,coverObj.getString("src"));
                        if(result != 1) throw new Exception("更新封面失败");
                    }
                }

                versionModel.setPublished(isPublish);
                versionModel.setVersion(version);
                versionModel.setHtml(html);
                versionModel.setDescription(description);
                versionModel.setRemark(remark);
                versionModel.setSaveType(save_type);
                versionModel.setCreatedTime(DateUtils.toLocalDateTime());
                versionModel.setCreatedUserId(userId);
                versionModel.setCreatedIp(ip);

                int result = answerVersionService.insert(versionModel);
                if(result < 1) throw new Exception("写入版本失败");
                result = answerDao.updateVersion(id,isPublish ? versionModel.getVersion() : null,versionModel.getVersion(),isPublish && !detailModel.isPublished() ? true : null,null,null,DateUtils.getTimestamp(),ip,userId);
                if(result != 1) throw new Exception("更新版本失败");
                // 更新冗余信息
                answerService.updateInfo(id,versionModel.getDescription(),DateUtils.getTimestamp(),ip,userId);
            }
            //吸入用户草稿
            String url = "/question/" + questionModel.getId() + "/answer/" + id.toString();
            String draftTitle = questionModel.getTitle();
            if(StringUtils.isEmpty(draftTitle)) draftTitle = "无标题";
            UserDraftModel draftModel = new UserDraftModel(detailModel.getCreatedUserId(),url,draftTitle,StringUtils.getFragmenter(versionModel.getContent()), EntityType.ANSWER,id,DateUtils.toLocalDateTime(),ip,userId);
            userDraftService.insertOrUpdate(draftModel);

            if(isPublish){
                if(!detailModel.isPublished()){
                    operationalPublisher.publish(new AnswerEvent(this, EntityAction.PUBLISH,detailModel.getQuestionId(),questionModel.getCreatedUserId(),
                            detailModel.getId(),detailModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));
                }else{
                    operationalPublisher.publish(new AnswerEvent(this, EntityAction.UPDATE,detailModel.getQuestionId(),questionModel.getCreatedUserId(),
                            detailModel.getId(),detailModel.getCreatedUserId(),userId, DateUtils.toLocalDateTime(),ip));
                }
            }
            return versionModel;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public QuestionAnswerVersionModel addVersion(Long id, Long questionId, Long userId, String content, String html,String description, String remark, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception {
        return addVersion(id,questionId,userId,content,html,description,remark,null,save_type,ip,isPublish,force);
    }
}
