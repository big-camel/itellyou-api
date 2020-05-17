package com.itellyou.service.sys.impl;

import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.column.ColumnSearchService;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.service.sys.EntityService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.user.UserSearchService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EntityServiceImpl implements EntityService {

    private final QuestionSearchService questionSearchService;
    private final QuestionAnswerSearchService answerSearchService;
    private final ArticleSearchService articleSearchService;
    private final ColumnSearchService columnSearchService;
    private final TagSearchService tagSearchService;
    private final UserSearchService userSearchService;

    public EntityServiceImpl(QuestionSearchService questionSearchService, QuestionAnswerSearchService answerSearchService, ArticleSearchService articleSearchService, ColumnSearchService columnSearchService, TagSearchService tagSearchService, UserSearchService userSearchService) {
        this.questionSearchService = questionSearchService;
        this.answerSearchService = answerSearchService;
        this.articleSearchService = articleSearchService;
        this.columnSearchService = columnSearchService;
        this.tagSearchService = tagSearchService;
        this.userSearchService = userSearchService;
    }

    @Override
    public Map<EntityType, Map<Long, Object>> find(Map<EntityType, HashSet<Long>> ids,Long searchId,Integer childCount) {
        Map<EntityType, Map<Long, Object>> mapData = new LinkedHashMap<>();
        if(ids.containsKey(EntityType.QUESTION)){
            List<QuestionDetailModel> list = questionSearchService.search(ids.get(EntityType.QUESTION),null,null,searchId,false,childCount,null,null,null,null,null);
            Map<Long, Object> map = new LinkedHashMap<>();
            for (QuestionDetailModel model : list){
                map.put(model.getId(),model);
            }
            mapData.put(EntityType.QUESTION,map);
        }
        if(ids.containsKey(EntityType.ANSWER)){
            List<QuestionAnswerDetailModel> list = answerSearchService.search(ids.get(EntityType.ANSWER),null,null,searchId,null,false,null,null,null,null,null);
            Map<Long, Object> map = new LinkedHashMap<>();
            for (QuestionAnswerDetailModel model : list){
                map.put(model.getId(),model);
            }
            mapData.put(EntityType.ANSWER,map);
        }
        if(ids.containsKey(EntityType.ARTICLE)){
            List<ArticleDetailModel> list = articleSearchService.search(ids.get(EntityType.ARTICLE),null,null,null,searchId,null,false,null,null,null,null,null);
            Map<Long, Object> map = new LinkedHashMap<>();
            for (ArticleDetailModel model : list){
                map.put(model.getId(),model);
            }
            mapData.put(EntityType.ARTICLE,map);
        }
        if(ids.containsKey(EntityType.COLUMN)){
            List<ColumnDetailModel> list = columnSearchService.search(ids.get(EntityType.COLUMN),null,null,null,searchId,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
            Map<Long, Object> map = new LinkedHashMap<>();
            for (ColumnDetailModel model : list){
                map.put(model.getId(),model);
            }
            mapData.put(EntityType.COLUMN,map);
        }
        if(ids.containsKey(EntityType.TAG)){
            List<TagDetailModel> list = tagSearchService.search(ids.get(EntityType.TAG),null,null,null,null,searchId,true,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
            Map<Long, Object> map = new LinkedHashMap<>();
            for (TagDetailModel model : list){
                map.put(model.getId(),model);
            }
            mapData.put(EntityType.TAG,map);
        }
        if(ids.containsKey(EntityType.USER)){
            List<UserDetailModel> list = userSearchService.search(ids.get(EntityType.USER),searchId,null,null,null,null,null,null,null,null,null,null);
            Map<Long, Object> map = new LinkedHashMap<>();
            for (UserDetailModel model : list){
                map.put(model.getId(),model);
            }
            mapData.put(EntityType.USER,map);
        }
        return mapData;
    }
}
