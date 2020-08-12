package com.itellyou.service.common.impl;

import com.itellyou.dao.common.OperationalDao;
import com.itellyou.model.software.SoftwareCommentDetailModel;
import com.itellyou.model.software.SoftwareDetailModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.common.OperationalDetailModel;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.article.ArticleCommentDetailModel;
import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.question.QuestionAnswerCommentDetailModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionCommentDetailModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.*;
import com.itellyou.service.article.ArticleCommentSearchService;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.column.ColumnSearchService;
import com.itellyou.service.question.*;
import com.itellyou.service.software.SoftwareCommentSearchService;
import com.itellyou.service.software.SoftwareSearchService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.common.OperationalService;
import com.itellyou.service.user.UserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OperationalServiceImpl implements OperationalService {

    private final OperationalDao operationalDao;
    private final UserSearchService userSearchService;
    private final QuestionSearchService questionSearchService;
    private final QuestionAnswerSearchService answerSearchService;
    private final ArticleSearchService articleSearchService;
    private final SoftwareSearchService softwareSearchService;
    private final ColumnSearchService columnSearchService;
    private final QuestionCommentSearchService questionCommentSearchService;
    private final QuestionAnswerCommentSearchService questionAnswerCommentSearchService;
    private final ArticleCommentSearchService articleCommentSearchService;
    private final SoftwareCommentSearchService softwareCommentSearchService;
    private final TagSearchService tagSearchService;

    @Autowired
    public OperationalServiceImpl(OperationalDao operationalDao, UserSearchService userSearchService, QuestionSearchService questionSearchService, QuestionAnswerSearchService answerSearchService, ArticleSearchService articleSearchService, SoftwareSearchService softwareSearchService, ColumnSearchService columnSearchService,
                                  QuestionCommentSearchService questionCommentSearchService, QuestionAnswerCommentSearchService questionAnswerCommentSearchService, ArticleCommentSearchService articleCommentSearchService,
                                  SoftwareCommentSearchService softwareCommentSearchService, TagSearchService tagSearchService){
        this.operationalDao = operationalDao;
        this.userSearchService = userSearchService;
        this.questionSearchService = questionSearchService;
        this.answerSearchService = answerSearchService;
        this.articleSearchService = articleSearchService;
        this.softwareSearchService = softwareSearchService;
        this.columnSearchService = columnSearchService;
        this.questionCommentSearchService = questionCommentSearchService;
        this.questionAnswerCommentSearchService = questionAnswerCommentSearchService;
        this.articleCommentSearchService = articleCommentSearchService;
        this.softwareCommentSearchService = softwareCommentSearchService;
        this.tagSearchService = tagSearchService;
    }

    @Override
    public int insert(OperationalModel model) {
        return operationalDao.insert(model);
    }

    @Override
    @Async
    public void insertAsync(OperationalModel model) {
        insert(model);
    }

    @Override
    public List<OperationalModel> search(Long id, Map<EntityAction, HashSet<EntityType>> actionsMap, Long targetUserId, Long userId, Boolean includeSelf, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return operationalDao.search(id,actionsMap,targetUserId,userId,includeSelf,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(Long id, Map<EntityAction, HashSet<EntityType>> actionsMap, Long targetUserId, Long userId, Boolean includeSelf, Long beginTime, Long endTime, Long ip) {
        return operationalDao.count(id,actionsMap,targetUserId,userId,includeSelf,beginTime,endTime,ip);
    }

    @Override
    public List<OperationalDetailModel> toDetail(List<OperationalModel> operationalModelList, Long searchUserId){
        List<OperationalDetailModel> operationalDetailModelList = new ArrayList<>();
        Map<EntityType,HashSet<Long>> dataMap = new LinkedHashMap<>();
        for (OperationalModel operationalModel:operationalModelList) {
            operationalDetailModelList.add(new OperationalDetailModel(operationalModel));
            EntityAction action = operationalModel.getAction();
            EntityType type = operationalModel.getType();
            Long targetId = operationalModel.getTargetId();
            if(action.equals(EntityAction.COMMENT)){
                if(type.equals(EntityType.QUESTION)) type = EntityType.QUESTION_COMMENT;
                if(type.equals(EntityType.ANSWER)) type = EntityType.ANSWER_COMMENT;
                if(type.equals(EntityType.ARTICLE)) type = EntityType.ARTICLE_COMMENT;
                if(type.equals(EntityType.SOFTWARE)) type = EntityType.SOFTWARE_COMMENT;
            }
            if(dataMap.containsKey(type)){
                HashSet<Long> hashSet = dataMap.get(type);
                if(!hashSet.contains(targetId))
                    hashSet.add(targetId);
            }else{
                dataMap.put(type,targetId != null ? new LinkedHashSet<Long>(){{ add(targetId);}} : null);
            }
        }

        if(dataMap.containsKey(EntityType.QUESTION_COMMENT)){
            List<QuestionCommentDetailModel> questionCommentModels = questionCommentSearchService.search(dataMap.get(EntityType.QUESTION_COMMENT),null,null,null,searchUserId,null,null,null,true,null,null,null,null,null,null,null,null,null,null,null,null);
            for (OperationalDetailModel operationalModel : operationalDetailModelList){
                EntityAction action = operationalModel.getAction();
                EntityType type = operationalModel.getType();
                if((action.equals(EntityAction.COMMENT) || action.equals(EntityAction.LIKE)) && (type.equals(EntityType.QUESTION) || type.equals(EntityType.QUESTION_COMMENT))){
                    for (QuestionCommentDetailModel detailModel : questionCommentModels){
                        if(operationalModel.getTargetId().equals(detailModel.getId())){
                            operationalModel.setTarget(detailModel);
                            if(dataMap.containsKey(EntityType.QUESTION)){
                                HashSet<Long> hashSet = dataMap.get(EntityType.QUESTION);
                                if(!hashSet.contains(detailModel.getQuestionId()))
                                    hashSet.add(detailModel.getQuestionId());
                            }else{
                                dataMap.put(EntityType.QUESTION,new LinkedHashSet<Long>(){{ add(detailModel.getQuestionId());}});
                            }
                            break;
                        }
                    }
                }
            }
        }
        if(dataMap.containsKey(EntityType.ANSWER_COMMENT)){
            List<QuestionAnswerCommentDetailModel> questionAnswerCommentModels = questionAnswerCommentSearchService.search(dataMap.get(EntityType.ANSWER_COMMENT),null,null,null,searchUserId,null,null,null,true,null,null,null,null,null,null,null,null,null,null,null,null);
            for (OperationalDetailModel operationalModel : operationalDetailModelList){
                EntityAction action = operationalModel.getAction();
                EntityType type = operationalModel.getType();
                if((action.equals(EntityAction.COMMENT) || action.equals(EntityAction.LIKE))  && (type.equals(EntityType.ANSWER) || type.equals(EntityType.ANSWER_COMMENT))){
                    for (QuestionAnswerCommentDetailModel detailModel : questionAnswerCommentModels){
                        if(operationalModel.getTargetId().equals(detailModel.getId())){
                            operationalModel.setTarget(detailModel);
                            if(dataMap.containsKey(EntityType.ANSWER)){
                                HashSet<Long> hashSet = dataMap.get(EntityType.ANSWER);
                                if(!hashSet.contains(detailModel.getAnswerId()))
                                    hashSet.add(detailModel.getAnswerId());
                            }else{
                                dataMap.put(EntityType.ANSWER,new LinkedHashSet<Long>(){{ add(detailModel.getAnswerId());}});
                            }
                            break;
                        }
                    }
                }
            }
        }
        if(dataMap.containsKey(EntityType.ARTICLE_COMMENT)){
            List<ArticleCommentDetailModel> articleCommentModels = articleCommentSearchService.search(dataMap.get(EntityType.ARTICLE_COMMENT),null,null,null,searchUserId,null,null,null,true,null,null,null,null,null,null,null,null,null,null,null,null);
            for (OperationalDetailModel operationalModel : operationalDetailModelList){
                EntityAction action = operationalModel.getAction();
                EntityType type = operationalModel.getType();
                if((action.equals(EntityAction.COMMENT) || action.equals(EntityAction.LIKE)) && (type.equals(EntityType.ARTICLE) || type.equals(EntityType.ARTICLE_COMMENT))){
                    for (ArticleCommentDetailModel detailModel : articleCommentModels){
                        if(operationalModel.getTargetId().equals(detailModel.getId())){
                            operationalModel.setTarget(detailModel);
                            if(dataMap.containsKey(EntityType.ARTICLE)){
                                HashSet<Long> hashSet = dataMap.get(EntityType.ARTICLE);
                                if(!hashSet.contains(detailModel.getArticleId()))
                                    hashSet.add(detailModel.getArticleId());
                            }else{
                                dataMap.put(EntityType.ARTICLE,new HashSet<Long>(){{ add(detailModel.getArticleId());}});
                            }
                            break;
                        }
                    }
                }
            }
        }
        if(dataMap.containsKey(EntityType.SOFTWARE_COMMENT)){
            List<SoftwareCommentDetailModel> softwareCommentModels = softwareCommentSearchService.search(dataMap.get(EntityType.SOFTWARE_COMMENT),null,null,null,searchUserId,null,null,null,true,null,null,null,null,null,null,null,null,null,null,null,null);
            for (OperationalDetailModel operationalModel : operationalDetailModelList){
                EntityAction action = operationalModel.getAction();
                EntityType type = operationalModel.getType();
                if((action.equals(EntityAction.COMMENT) || action.equals(EntityAction.LIKE)) && (type.equals(EntityType.SOFTWARE) || type.equals(EntityType.SOFTWARE_COMMENT))){
                    for (SoftwareCommentDetailModel detailModel : softwareCommentModels){
                        if(operationalModel.getTargetId().equals(detailModel.getId())){
                            operationalModel.setTarget(detailModel);
                            if(dataMap.containsKey(EntityType.SOFTWARE)){
                                HashSet<Long> hashSet = dataMap.get(EntityType.SOFTWARE);
                                if(!hashSet.contains(detailModel.getSoftwareId()))
                                    hashSet.add(detailModel.getSoftwareId());
                            }else{
                                dataMap.put(EntityType.SOFTWARE,new HashSet<Long>(){{ add(detailModel.getSoftwareId());}});
                            }
                            break;
                        }
                    }
                }
            }
        }
        if(dataMap.containsKey(EntityType.ANSWER)){
            List<QuestionAnswerDetailModel> answerModels = answerSearchService.search(dataMap.get(EntityType.ANSWER),null,"version",searchUserId,null,true,null,null,null,null,null);
            for (OperationalDetailModel operationalModel : operationalDetailModelList){
                EntityAction action = operationalModel.getAction();
                EntityType type = operationalModel.getType();
                if(!action.equals(EntityAction.COMMENT) && type.equals(EntityType.ANSWER)){
                    for (QuestionAnswerDetailModel detailModel : answerModels){
                        if(operationalModel.getTargetId().equals(detailModel.getId())){
                            operationalModel.setTarget(detailModel);
                            if(dataMap.containsKey(EntityType.QUESTION)){
                                HashSet<Long> hashSet = dataMap.get(EntityType.QUESTION);
                                if(!hashSet.contains(detailModel.getQuestionId()))
                                    hashSet.add(detailModel.getQuestionId());
                            }else{
                                dataMap.put(EntityType.QUESTION,new HashSet<Long>(){{ add(detailModel.getQuestionId());}});
                            }
                            break;
                        }
                    }
                }else if((action.equals(EntityAction.COMMENT) || action.equals(EntityAction.LIKE)) && (type.equals(EntityType.ANSWER) || type.equals(EntityType.ANSWER_COMMENT))){
                    Object target = operationalModel.getTarget();
                    if(target == null) continue;
                    QuestionAnswerCommentDetailModel commentDetailModel = (QuestionAnswerCommentDetailModel)target;
                    for (QuestionAnswerDetailModel detailModel : answerModels){
                        if(commentDetailModel.getAnswerId().equals(detailModel.getId())){
                            if(type.equals(EntityType.ANSWER) || commentDetailModel.getReply() == null){
                                commentDetailModel.setAnswer(detailModel);
                            }else{
                                commentDetailModel.getReply().setAnswer(detailModel);
                            }
                            break;
                        }
                    }
                }
            }
        }

        if(dataMap.containsKey(EntityType.QUESTION)){
            List<QuestionDetailModel> questionModels = questionSearchService.search(dataMap.get(EntityType.QUESTION),"version",null,searchUserId,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
            for (OperationalDetailModel operationalModel : operationalDetailModelList){
                EntityAction action = operationalModel.getAction();
                EntityType type = operationalModel.getType();
                if(!action.equals(EntityAction.COMMENT) && type.equals(EntityType.QUESTION)){
                    for (QuestionDetailModel detailModel : questionModels){
                        if(operationalModel.getTargetId().equals(detailModel.getId())){
                            operationalModel.setTarget(detailModel);
                            break;
                        }
                    }
                }else if((action.equals(EntityAction.COMMENT) || action.equals(EntityAction.LIKE))  && (type.equals(EntityType.QUESTION) || type.equals(EntityType.QUESTION_COMMENT))){
                    Object target = operationalModel.getTarget();
                    if(target == null) continue;
                    QuestionCommentDetailModel commentDetailModel = (QuestionCommentDetailModel)target;
                    for (QuestionDetailModel detailModel : questionModels){
                        if(commentDetailModel.getQuestionId().equals(detailModel.getId())){
                            if(type.equals(EntityType.QUESTION) || commentDetailModel.getReply() == null) {
                                commentDetailModel.setQuestion(detailModel);
                            }else {
                                commentDetailModel.getReply().setQuestion(detailModel);
                            }
                            break;
                        }
                    }
                }else if(!action.equals(EntityAction.COMMENT) && operationalModel.getType().equals(EntityType.ANSWER)){
                    Object target = operationalModel.getTarget();
                    if(target == null) continue;
                    QuestionAnswerDetailModel answerDetailModel = (QuestionAnswerDetailModel)target;
                    for (QuestionDetailModel detailModel : questionModels){
                        if(answerDetailModel.getQuestionId().equals(detailModel.getId())){
                            answerDetailModel.setQuestion(detailModel);
                            break;
                        }
                    }
                }else if((action.equals(EntityAction.COMMENT) || action.equals(EntityAction.LIKE))  && (type.equals(EntityType.ANSWER) || type.equals(EntityType.ANSWER_COMMENT))) {
                    Object target = operationalModel.getTarget();
                    if (target == null) continue;
                    QuestionAnswerCommentDetailModel answerCommentDetailModel = (QuestionAnswerCommentDetailModel)target;
                    QuestionAnswerDetailModel answerDetailModel = type.equals(EntityType.ANSWER) || answerCommentDetailModel.getReply() == null ? answerCommentDetailModel.getAnswer() : answerCommentDetailModel.getReply().getAnswer();
                    if(answerDetailModel == null) continue;
                    for (QuestionDetailModel detailModel : questionModels){
                        if(answerDetailModel.getQuestionId().equals(detailModel.getId())){
                            answerDetailModel.setQuestion(detailModel);
                            break;
                        }
                    }
                }
            }

        }

        if(dataMap.containsKey(EntityType.ARTICLE)) {
            List<ArticleDetailModel> articleModels = articleSearchService.search(dataMap.get(EntityType.ARTICLE),"version",null,null,searchUserId,null,true,null,null,null,null,null);
            for (OperationalDetailModel operationalModel : operationalDetailModelList){
                EntityAction action = operationalModel.getAction();
                EntityType type = operationalModel.getType();
                if(!action.equals(EntityAction.COMMENT) && type.equals(EntityType.ARTICLE)){
                    for (ArticleDetailModel detailModel : articleModels){
                        if(operationalModel.getTargetId().equals(detailModel.getId())){
                            operationalModel.setTarget(detailModel);
                            break;
                        }
                    }
                }
                else if((action.equals(EntityAction.COMMENT) || action.equals(EntityAction.LIKE))  && (type.equals(EntityType.ARTICLE) || type.equals(EntityType.ARTICLE_COMMENT))){
                    Object target = operationalModel.getTarget();
                    if (target == null) continue;
                    ArticleCommentDetailModel commentDetailModel = (ArticleCommentDetailModel) target;
                    for (ArticleDetailModel detailModel : articleModels) {
                        if (commentDetailModel.getArticleId().equals(detailModel.getId())) {
                            if(type.equals(EntityType.ARTICLE) || commentDetailModel.getReply() == null) {
                                commentDetailModel.setArticle(detailModel);
                            }else {
                                commentDetailModel.getReply().setArticle(detailModel);
                            }
                            break;
                        }
                    }
                }
            }
        }

        if(dataMap.containsKey(EntityType.SOFTWARE)) {
            List<SoftwareDetailModel> softwareModels = softwareSearchService.search(dataMap.get(EntityType.SOFTWARE),"version",null,null,searchUserId,true,null,null,null,null,null);
            for (OperationalDetailModel operationalModel : operationalDetailModelList){
                EntityAction action = operationalModel.getAction();
                EntityType type = operationalModel.getType();
                if(!action.equals(EntityAction.COMMENT) && type.equals(EntityType.SOFTWARE)){
                    for (SoftwareDetailModel detailModel : softwareModels){
                        if(operationalModel.getTargetId().equals(detailModel.getId())){
                            operationalModel.setTarget(detailModel);
                            break;
                        }
                    }
                }
                else if((action.equals(EntityAction.COMMENT) || action.equals(EntityAction.LIKE))  && (type.equals(EntityType.SOFTWARE) || type.equals(EntityType.SOFTWARE_COMMENT))){
                    Object target = operationalModel.getTarget();
                    if (target == null) continue;
                    SoftwareCommentDetailModel commentDetailModel = (SoftwareCommentDetailModel) target;
                    for (SoftwareDetailModel detailModel : softwareModels) {
                        if (commentDetailModel.getSoftwareId().equals(detailModel.getId())) {
                            if(type.equals(EntityType.SOFTWARE) || commentDetailModel.getReply() == null) {
                                commentDetailModel.setSoftware(detailModel);
                            }else {
                                commentDetailModel.getReply().setSoftware(detailModel);
                            }
                            break;
                        }
                    }
                }
            }
        }
        for (Map.Entry<EntityType,HashSet<Long>> entry:dataMap.entrySet()) {
            switch (entry.getKey()){
                case USER:
                    List<UserDetailModel> userModels = userSearchService.search(entry.getValue(),searchUserId,null,null,null,null,null,null,null,null,null,null);
                    for (OperationalDetailModel operationalModel : operationalDetailModelList){
                        if(operationalModel.getType().equals(entry.getKey())){
                            for (UserDetailModel userModel : userModels){
                                if(operationalModel.getTargetId().equals(userModel.getId())){
                                    operationalModel.setTarget(userModel);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case COLUMN:
                    List<ColumnDetailModel> columnModels = columnSearchService.search(entry.getValue(),null,null,null,searchUserId,null,true,null,null,null,null,null,null,null,null,null,null,null,null);
                    for (OperationalDetailModel operationalModel : operationalDetailModelList){
                        if(operationalModel.getType().equals(entry.getKey())){
                            for (ColumnDetailModel detailModel : columnModels){
                                if(operationalModel.getTargetId().equals(detailModel.getId())){
                                    operationalModel.setTarget(detailModel);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case TAG:
                    List<TagDetailModel> tagModels = tagSearchService.search(entry.getValue(),null,"version",null,null,searchUserId,true,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
                    for (OperationalDetailModel operationalModel : operationalDetailModelList){
                        if(operationalModel.getType().equals(entry.getKey())){
                            for (TagDetailModel detailModel : tagModels){
                                if(operationalModel.getTargetId().equals(detailModel.getId())){
                                    operationalModel.setTarget(detailModel);
                                    break;
                                }
                            }
                        }
                    }
                    break;
            }
        }
        return operationalDetailModelList;
    }

    @Override
    public List<OperationalDetailModel> searchDetail(Long id, Map<EntityAction, HashSet<EntityType>> actionsMap, Long targetUserId, Long userId, Boolean includeSelf, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<OperationalModel> operationalModelList = search(id,actionsMap,targetUserId,userId,includeSelf,beginTime,endTime,ip,order,offset,limit);
        return toDetail(operationalModelList,userId);
    }

    @Override
    public int deleteByTargetId(EntityAction action, EntityType type, Long userId, Long targetId) {
        return operationalDao.deleteByTargetId(action,type,userId,targetId);
    }

    @Override
    @Async
    public void deleteByTargetIdAsync(EntityAction action, EntityType type, Long userId, Long targetId) {
        deleteByTargetId(action,type,userId,targetId);
    }

}
