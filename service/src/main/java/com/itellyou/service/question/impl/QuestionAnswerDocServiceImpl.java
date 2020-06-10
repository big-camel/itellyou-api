package com.itellyou.service.question.impl;

import com.alibaba.fastjson.JSONObject;
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
import com.itellyou.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = "question_answer")
@Service
public class QuestionAnswerDocServiceImpl implements QuestionAnswerDocService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionAnswerService answerService;
    private final QuestionSearchService questionSearchService;
    private final QuestionInfoService questionInfoService;
    private final QuestionAnswerSingleService answerSingleService;
    private final QuestionAnswerSearchService answerSearchService;
    private final QuestionAnswerVersionService answerVersionService;
    private final UserInfoService userInfoService;
    private final UserDraftService userDraftService;
    private final OperationalPublisher operationalPublisher;

    public QuestionAnswerDocServiceImpl(QuestionAnswerService answerService, QuestionSearchService questionSearchService, QuestionInfoService questionInfoService, QuestionAnswerSingleService answerSingleService, QuestionAnswerSearchService answerSearchService, QuestionAnswerVersionService answerVersionService, UserInfoService userInfoService, UserDraftService userDraftService, OperationalPublisher operationalPublisher) {
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
    @CacheEvict(value = "question" , key = "#questionId")
    public Long create(Long questionId, Long userId, String content, String html,String description, String remark, String save_type, Long ip) throws Exception {

        try{
            QuestionAnswerModel answerModel = new QuestionAnswerModel();
            answerModel.setQuestionId(questionId);
            answerModel.setDraft(0);
            answerModel.setCreatedIp(ip);
            answerModel.setCreatedTime(DateUtils.getTimestamp());
            answerModel.setCreatedUserId(userId);
            int resultRows = answerService.insert(answerModel);
            if(resultRows != 1)
                throw new Exception("写入回答失败");
            QuestionAnswerVersionModel versionModel = addVersion(answerModel.getId(),questionId,userId,content,html,description,remark,1,save_type,ip,false,true);
            if(versionModel == null)
                throw new Exception("写入版本失败");
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
            QuestionAnswerModel answerModel = null;
            QuestionAnswerVersionModel versionModel = new QuestionAnswerVersionModel();
            versionModel.setAnswerId(id);
            if (force) {
                versionModel.setContent(content);
                answerModel = answerSingleService.findById(id);
            } else {
                QuestionAnswerDetailModel detailModel = answerSearchService.getDetail(id, questionId, "draft", null,userId);
                if (StringUtils.isNotEmpty(content) && detailModel != null && !content.equals(detailModel.getContent())) {
                    versionModel.setContent(content);
                }
                answerModel = detailModel;
            }
            if(answerModel == null || !answerModel.getQuestionId().equals(questionId)){
                return null;
            }

            // 当有新内容更新时才需要新增版本
            if (StringUtils.isNotEmpty(versionModel.getContent())) {

                if(isPublish == true && !answerModel.isPublished()){
                    int result = questionInfoService.updateAnswers(questionId,1);
                    if(result != 1) throw new Exception("更新提问回答数量失败");
                    result = userInfoService.updateAnswerCount(answerModel.getCreatedUserId(),1);
                    if(result != 1) throw new Exception("更新用户回答数量失败");
                }

                if(isPublish && StringUtils.isEmpty(answerModel.getCover())){
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
                versionModel.setCreatedTime(DateUtils.getTimestamp());
                versionModel.setCreatedUserId(userId);
                versionModel.setCreatedIp(ip);

                int rows = isPublish == true ? answerVersionService.updateVersion(versionModel) : answerVersionService.updateDraft(versionModel);
                if (rows != 1)
                    throw new Exception("新增版本失败");

                // 更新冗余信息
                answerService.updateInfo(id,versionModel.getDescription(),DateUtils.getTimestamp(),ip,userId);
            }
            String url = "/question/" + questionModel.getId() + "/answer/" + id.toString();
            String draftTitle = questionModel.getTitle();
            if(StringUtils.isEmpty(draftTitle)) draftTitle = "无标题";
            UserDraftModel draftModel = new UserDraftModel(answerModel.getCreatedUserId(),url,draftTitle,StringUtils.getFragmenter(versionModel.getContent()), EntityType.ANSWER,id,DateUtils.getTimestamp(),ip,userId);
            userDraftService.insertOrUpdate(draftModel);

            if(isPublish){
                if(!answerModel.isPublished()){
                    operationalPublisher.publish(new AnswerEvent(this, EntityAction.PUBLISH,
                            answerModel.getId(),answerModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
                }else{
                    operationalPublisher.publish(new AnswerEvent(this, EntityAction.UPDATE,
                            answerModel.getId(),answerModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
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
