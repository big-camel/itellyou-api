package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserOperationalDao;
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
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.user.UserOperationalService;
import com.itellyou.service.user.UserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserOperationalServiceImpl implements UserOperationalService {

    private final UserOperationalDao operationalDao;
    private final UserSearchService userSearchService;
    private final QuestionSearchService questionSearchService;
    private final QuestionAnswerSearchService answerSearchService;
    private final ArticleSearchService articleSearchService;
    private final ColumnSearchService columnSearchService;
    private final QuestionCommentSearchService questionCommentSearchService;
    private final QuestionAnswerCommentSearchService questionAnswerCommentSearchService;
    private final ArticleCommentSearchService articleCommentSearchService;
    private final TagSearchService tagSearchService;

    @Autowired
    public UserOperationalServiceImpl(UserOperationalDao operationalDao,UserSearchService userSearchService,QuestionSearchService questionSearchService,QuestionAnswerSearchService answerSearchService,ArticleSearchService articleSearchService,ColumnSearchService columnSearchService,
                                      QuestionCommentSearchService questionCommentSearchService,QuestionAnswerCommentSearchService questionAnswerCommentSearchService,ArticleCommentSearchService articleCommentSearchService,
                                      TagSearchService tagSearchService){
        this.operationalDao = operationalDao;
        this.userSearchService = userSearchService;
        this.questionSearchService = questionSearchService;
        this.answerSearchService = answerSearchService;
        this.articleSearchService = articleSearchService;
        this.columnSearchService = columnSearchService;
        this.questionCommentSearchService = questionCommentSearchService;
        this.questionAnswerCommentSearchService = questionAnswerCommentSearchService;
        this.articleCommentSearchService = articleCommentSearchService;
        this.tagSearchService = tagSearchService;
    }

    @Override
    public int insert(UserOperationalModel model) {
        return operationalDao.insert(model);
    }

    @Override
    @Async
    public void insertAsync(UserOperationalModel model) {
        insert(model);
    }

    @Override
    public List<UserOperationalModel> search(Long id, Map<UserOperationalAction, HashSet<EntityType>> actionsMap, Long targetUserId, Long userId,Boolean includeSelf, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return operationalDao.search(id,actionsMap,targetUserId,userId,includeSelf,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(Long id, Map<UserOperationalAction, HashSet<EntityType>> actionsMap, Long targetUserId, Long userId,Boolean includeSelf, Long beginTime, Long endTime, Long ip) {
        return operationalDao.count(id,actionsMap,targetUserId,userId,includeSelf,beginTime,endTime,ip);
    }

    @Override
    public List<UserOperationalDetailModel> toDetail(List<UserOperationalModel> operationalModelList,Long searchUserId){
        List<UserOperationalDetailModel> operationalDetailModelList = new ArrayList<>();
        Map<EntityType,HashSet<Long>> dataMap = new LinkedHashMap<>();
        for (UserOperationalModel operationalModel:operationalModelList) {
            operationalDetailModelList.add(new UserOperationalDetailModel(operationalModel));
            UserOperationalAction action = operationalModel.getAction();
            EntityType type = operationalModel.getType();
            Long targetId = operationalModel.getTargetId();
            if(action.equals(UserOperationalAction.COMMENT)){
                if(type.equals(EntityType.QUESTION)) type = EntityType.QUESTION_COMMENT;
                if(type.equals(EntityType.ANSWER)) type = EntityType.ANSWER_COMMENT;
                if(type.equals(EntityType.ARTICLE)) type = EntityType.ARTICLE_COMMENT;
            }
            if(dataMap.containsKey(type)){
                HashSet<Long> hashSet = dataMap.get(type);
                if(!hashSet.contains(targetId))
                    hashSet.add(targetId);
            }else{
                dataMap.put(type,new LinkedHashSet<Long>(){{ add(targetId);}});
            }
        }

        if(dataMap.containsKey(EntityType.QUESTION_COMMENT)){
            List<QuestionCommentDetailModel> questionCommentModels = questionCommentSearchService.search(dataMap.get(EntityType.QUESTION_COMMENT),null,null,null,searchUserId,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
            for (UserOperationalDetailModel operationalModel : operationalDetailModelList){
                UserOperationalAction action = operationalModel.getAction();
                EntityType type = operationalModel.getType();
                if((action.equals(UserOperationalAction.COMMENT) || action.equals(UserOperationalAction.LIKE)) && (type.equals(EntityType.QUESTION) || type.equals(EntityType.QUESTION_COMMENT))){
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
            List<QuestionAnswerCommentDetailModel> questionAnswerCommentModels = questionAnswerCommentSearchService.search(dataMap.get(EntityType.ANSWER_COMMENT),null,null,null,searchUserId,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
            for (UserOperationalDetailModel operationalModel : operationalDetailModelList){
                UserOperationalAction action = operationalModel.getAction();
                EntityType type = operationalModel.getType();
                if((action.equals(UserOperationalAction.COMMENT) || action.equals(UserOperationalAction.LIKE))  && (type.equals(EntityType.ANSWER) || type.equals(EntityType.ANSWER_COMMENT))){
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
            List<ArticleCommentDetailModel> articleCommentModels = articleCommentSearchService.search(dataMap.get(EntityType.ARTICLE_COMMENT),null,null,null,searchUserId,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
            for (UserOperationalDetailModel operationalModel : operationalDetailModelList){
                UserOperationalAction action = operationalModel.getAction();
                EntityType type = operationalModel.getType();
                if((action.equals(UserOperationalAction.COMMENT) || action.equals(UserOperationalAction.LIKE)) && (type.equals(EntityType.ARTICLE) || type.equals(EntityType.ARTICLE_COMMENT))){
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
        if(dataMap.containsKey(EntityType.ANSWER)){
            List<QuestionAnswerDetailModel> answerModels = answerSearchService.search(dataMap.get(EntityType.ANSWER),null,"version",searchUserId,null,true,null,null,null,null,null);
            for (UserOperationalDetailModel operationalModel : operationalDetailModelList){
                UserOperationalAction action = operationalModel.getAction();
                EntityType type = operationalModel.getType();
                if(!action.equals(UserOperationalAction.COMMENT) && type.equals(EntityType.ANSWER)){
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
                }else if((action.equals(UserOperationalAction.COMMENT) || action.equals(UserOperationalAction.LIKE)) && (type.equals(EntityType.ANSWER) || type.equals(EntityType.ANSWER_COMMENT))){
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
            for (UserOperationalDetailModel operationalModel : operationalDetailModelList){
                UserOperationalAction action = operationalModel.getAction();
                EntityType type = operationalModel.getType();
                if(!action.equals(UserOperationalAction.COMMENT) && type.equals(EntityType.QUESTION)){
                    for (QuestionDetailModel detailModel : questionModels){
                        if(operationalModel.getTargetId().equals(detailModel.getId())){
                            operationalModel.setTarget(detailModel);
                            break;
                        }
                    }
                }else if((action.equals(UserOperationalAction.COMMENT) || action.equals(UserOperationalAction.LIKE))  && (type.equals(EntityType.QUESTION) || type.equals(EntityType.QUESTION_COMMENT))){
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
                }else if(!action.equals(UserOperationalAction.COMMENT) && operationalModel.getType().equals(EntityType.ANSWER)){
                    Object target = operationalModel.getTarget();
                    if(target == null) continue;
                    QuestionAnswerDetailModel answerDetailModel = (QuestionAnswerDetailModel)target;
                    for (QuestionDetailModel detailModel : questionModels){
                        if(answerDetailModel.getQuestionId().equals(detailModel.getId())){
                            answerDetailModel.setQuestion(detailModel);
                            break;
                        }
                    }
                }else if((action.equals(UserOperationalAction.COMMENT) || action.equals(UserOperationalAction.LIKE))  && (type.equals(EntityType.ANSWER) || type.equals(EntityType.ANSWER_COMMENT))) {
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
            for (UserOperationalDetailModel operationalModel : operationalDetailModelList){
                UserOperationalAction action = operationalModel.getAction();
                EntityType type = operationalModel.getType();
                if(!action.equals(UserOperationalAction.COMMENT) && type.equals(EntityType.ARTICLE)){
                    for (ArticleDetailModel detailModel : articleModels){
                        if(operationalModel.getTargetId().equals(detailModel.getId())){
                            operationalModel.setTarget(detailModel);
                            break;
                        }
                    }
                }
                else if((action.equals(UserOperationalAction.COMMENT) || action.equals(UserOperationalAction.LIKE))  && (type.equals(EntityType.ARTICLE) || type.equals(EntityType.ARTICLE_COMMENT))){
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
        for (Map.Entry<EntityType,HashSet<Long>> entry:dataMap.entrySet()) {
            switch (entry.getKey()){
                case USER:
                    List<UserDetailModel> userModels = userSearchService.search(entry.getValue(),searchUserId,null,null,null,null,null,null,null,null,null,null);
                    for (UserOperationalDetailModel operationalModel : operationalDetailModelList){
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
                    for (UserOperationalDetailModel operationalModel : operationalDetailModelList){
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
                    for (UserOperationalDetailModel operationalModel : operationalDetailModelList){
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
    public List<UserOperationalDetailModel> searchDetail(Long id, Map<UserOperationalAction, HashSet<EntityType>> actionsMap, Long targetUserId, Long userId,Boolean includeSelf, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<UserOperationalModel> operationalModelList = search(id,actionsMap,targetUserId,userId,includeSelf,beginTime,endTime,ip,order,offset,limit);
        return toDetail(operationalModelList,userId);
    }

    @Override
    public int deleteByTargetId(UserOperationalAction action, EntityType type, Long userId, Long targetId) {
        return operationalDao.deleteByTargetId(action,type,userId,targetId);
    }

    @Override
    @Async
    public void deleteByTargetIdAsync(UserOperationalAction action, EntityType type, Long userId, Long targetId) {
        deleteByTargetId(action,type,userId,targetId);
    }

}
