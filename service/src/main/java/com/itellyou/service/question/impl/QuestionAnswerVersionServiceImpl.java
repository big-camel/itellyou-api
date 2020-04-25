package com.itellyou.service.question.impl;

import com.alibaba.fastjson.JSONObject;
import com.itellyou.dao.question.QuestionAnswerDao;
import com.itellyou.dao.question.QuestionAnswerVersionDao;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.question.*;
import com.itellyou.model.user.*;
import com.itellyou.service.question.*;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.service.user.UserOperationalService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionAnswerVersionServiceImpl implements QuestionAnswerVersionService {

    private final QuestionAnswerVersionDao versionDao;
    private final QuestionAnswerDao answerDao;
    private final QuestionAnswerIndexService answerIndexService;
    private final QuestionSearchService questionSearchService;
    private final QuestionInfoService questionInfoService;
    private final QuestionIndexService questionIndexService;
    private final QuestionAnswerSearchService answerSearchService;
    private final UserInfoService userService;
    private final UserDraftService draftService;
    private final UserOperationalService operationalService;

    @Autowired
    public QuestionAnswerVersionServiceImpl(QuestionAnswerVersionDao versionDao,QuestionAnswerDao answerDao,QuestionAnswerIndexService answerIndexService,QuestionSearchService questionSearchService,QuestionInfoService questionInfoService,QuestionIndexService questionIndexService,QuestionAnswerSearchService answerSearchService,UserInfoService userService,UserDraftService draftService,UserOperationalService operationalService){
        this.versionDao = versionDao;
        this.answerDao = answerDao;
        this.answerIndexService = answerIndexService;
        this.questionSearchService = questionSearchService;
        this.questionInfoService = questionInfoService;
        this.questionIndexService = questionIndexService;
        this.answerSearchService = answerSearchService;
        this.userService = userService;
        this.draftService = draftService;
        this.operationalService = operationalService;
    }

    @Override
    public int insert(QuestionAnswerVersionModel versionModel) {
        int rows = versionDao.insert(versionModel);
        if(rows != 1){
            return 0;
        }
        Integer version = findVersionById(versionModel.getId());
        versionModel.setVersion(version);
        return 1;
    }

    @Override
    public Integer findVersionById(Long id) {
        return versionDao.findVersionById(id);
    }

    @Override
    public List<QuestionAnswerVersionModel> searchByAnswerId(Long answerId, Long questionId, Boolean hasContent) {
        Map<String, String> order = new HashMap<>();
        order.put("created_time","desc");
        return versionDao.search(null,answerId,questionId,null,hasContent,null,null,null,null,null,null,order,null,null);
    }

    @Override
    public List<QuestionAnswerVersionModel> searchByAnswerId(Long answerId, Boolean hasContent) {
        return searchByAnswerId(answerId,null,hasContent);
    }

    @Override
    public List<QuestionAnswerVersionModel> searchByAnswerId(Long answerId, Long questionId) {
        return searchByAnswerId(answerId,questionId,null);
    }

    @Override
    public List<QuestionAnswerVersionModel> searchByAnswerId(Long answerId) {
        return searchByAnswerId(answerId,false);
    }

    @Override
    public QuestionAnswerVersionModel findById(Long id) {
        return findByAnswerIdAndId(id,null);
    }

    @Override
    public QuestionAnswerVersionModel findByAnswerIdAndId(Long id, Long answerId) {
        return findByAnswerIdAndId(id,answerId,null);
    }

    @Override
    public QuestionAnswerVersionModel findByAnswerIdAndId(Long id, Long answerId, Long questionId) {
        List<QuestionAnswerVersionModel> list = versionDao.search(id,answerId,questionId,null,true,null,null,null,null,null,null,null,null,null);
        if(list == null || list.size() == 0)
            return null;
        return list.get(0);
    }


    @Override
    public int updateVersion(Long id, Integer version, Long ip, Long user) {
        return updateVersion(id,version,false,null,null,ip,user);
    }

    @Override
    public int updateVersion(Long id, Integer version, Boolean isPublished, Boolean isDisabled,Boolean isDeleted, Long ip, Long user) {
        return updateVersion(id,version,null,isPublished,isDisabled,isDeleted, DateUtils.getTimestamp(),ip,user);
    }

    @Override
    public int updateVersion(Long id, Integer version, Integer draft, Boolean isPublished, Boolean isDisabled,Boolean isDeleted, Long time, Long ip, Long user) {
        return answerDao.updateVersion(id,version,draft,isPublished,isDisabled,isDeleted,time,ip,user);
    }

    @Override
    @Transactional
    public int updateVersion(QuestionAnswerVersionModel versionModel) {
        try{
            int result = insert(versionModel);
            if(result != 1)
            {
                throw new Exception("写入版本失败");
            }
            result = updateVersion(versionModel.getAnswerId(),versionModel.getVersion(),versionModel.getVersion(),versionModel.isPublished(),null,null,versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
            if(result != 1)
            {
                throw new Exception("更新版本号失败");
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    public int updateDraft(Long id, Integer version, Boolean isPublished, Boolean isDisabled,Boolean isDeleted, Long time, Long ip, Long user) {
        // 更新草稿版本不更新时间/ip/和用户编号
        return updateVersion(id,null,version,isPublished,isDisabled,isDeleted,null,null,null);
    }

    @Override
    public int updateDraft(Long id, Integer version, Long time, Long ip, Long user) {
        return updateDraft(id,version,null,null,null,time,ip,user);
    }

    @Override
    @Transactional
    public int updateDraft(QuestionAnswerVersionModel versionModel) {
        try{
            int result = insert(versionModel);
            if(result != 1)
            {
                throw new Exception("写入版本失败");
            }
            result = updateDraft(versionModel.getAnswerId(),versionModel.getVersion(),null,null,null,versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
            if(result != 1)
            {
                throw new Exception("更新草稿版本号失败");
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }


    @Override
    @Transactional
    public QuestionAnswerVersionModel addVersion(Long id,Long questionId, Long userId, String content, String html,String description,String remark,Integer version, String save_type, Long ip,Boolean isPublish,Boolean force) throws Exception {
        try {
            QuestionDetailModel questionModel = questionSearchService.getDetail(questionId);
            if(questionModel == null) throw new Exception("错误的提问");
            QuestionAnswerModel answerModel = null;
            QuestionAnswerVersionModel versionModel = new QuestionAnswerVersionModel();
            versionModel.setAnswerId(id);
            if (force) {
                versionModel.setContent(content);
                answerModel = answerSearchService.findById(id);
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
                    result = userService.updateAnswerCount(answerModel.getCreatedUserId(),1);
                    if(result != 1) throw new Exception("更新用户回答数量失败");
                }

                if(isPublish && StringUtils.isEmpty(answerModel.getCover())){
                    JSONObject coverObj = StringUtils.getEditorContentCover(content);
                    if(coverObj != null){
                        int result = answerDao.updateMetas(id,coverObj.getString("src"));
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

                int rows = isPublish == true ? updateVersion(versionModel) : updateDraft(versionModel);
                if (rows != 1)
                    throw new Exception("新增版本失败");
            }
            String url = "/question/" + questionModel.getId() + "/answer/" + id.toString();
            String draftTitle = questionModel.getTitle();
            if(StringUtils.isEmpty(draftTitle)) draftTitle = "无标题";
            UserDraftModel draftModel = new UserDraftModel(answerModel.getCreatedUserId(),url,draftTitle,StringUtils.getFragmenter(versionModel.getContent()), EntityType.ANSWER,id,DateUtils.getTimestamp(),ip,userId);
            draftService.insertOrUpdate(draftModel);

            if(isPublish){
                if(!answerModel.isPublished()){
                    questionIndexService.updateIndex(questionId);
                    operationalService.insertAsync(new UserOperationalModel(UserOperationalAction.PUBLISH, EntityType.ANSWER,answerModel.getId(),answerModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
                }

                QuestionAnswerDetailModel detailModel = answerSearchService.getDetail(id);
                answerIndexService.updateIndex(detailModel);

            }
            return versionModel;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    public QuestionAnswerVersionModel addVersion(Long id, Long questionId, Long userId, String content, String html,String description, String remark, String save_type, Long ip, Boolean isPublish, Boolean force) throws Exception {
        return addVersion(id,questionId,userId,content,html,description,remark,null,save_type,ip,isPublish,force);
    }
}
