package com.itellyou.service.sys.impl;

import com.itellyou.model.sys.EntityType;
import com.itellyou.service.article.impl.ArticleSearchServiceImpl;
import com.itellyou.service.column.impl.ColumnSearchServiceImpl;
import com.itellyou.service.question.impl.QuestionAnswerSearchServiceImpl;
import com.itellyou.service.question.impl.QuestionSearchServiceImpl;
import com.itellyou.service.software.impl.SoftwareSearchServiceImpl;
import com.itellyou.service.sys.EntitySearchService;
import com.itellyou.service.tag.impl.TagSearchServiceImpl;
import com.itellyou.service.user.impl.UserSearchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class EntitySearchFactory {

    private ArticleSearchServiceImpl articleSearchService;
    private QuestionSearchServiceImpl questionSearchService;
    private QuestionAnswerSearchServiceImpl answerSearchService;
    private TagSearchServiceImpl tagSearchService;
    private ColumnSearchServiceImpl columnSearchService;
    private UserSearchServiceImpl userSearchService;
    private SoftwareSearchServiceImpl softwareSearchService;

    private static EntitySearchFactory instance = new EntitySearchFactory();
    
    public EntitySearchFactory(){}

    @Autowired
    public EntitySearchFactory(ArticleSearchServiceImpl articleSearchService, QuestionSearchServiceImpl questionSearchService, QuestionAnswerSearchServiceImpl answerSearchService, TagSearchServiceImpl tagSearchService, ColumnSearchServiceImpl columnSearchService, UserSearchServiceImpl userSearchService, SoftwareSearchServiceImpl softwareSearchService){
        this.articleSearchService = articleSearchService;
        this.questionSearchService = questionSearchService;
        this.answerSearchService = answerSearchService;
        this.tagSearchService = tagSearchService;
        this.columnSearchService = columnSearchService;
        this.userSearchService = userSearchService;
        this.softwareSearchService = softwareSearchService;
    }


    @PostConstruct
    public void init(){
        instance = this;
        instance.articleSearchService = this.articleSearchService;
        instance.answerSearchService = this.answerSearchService;
        instance.questionSearchService = this.questionSearchService;
        instance.tagSearchService = this.tagSearchService;
        instance.columnSearchService = this.columnSearchService;
        instance.userSearchService = this.userSearchService;
        instance.softwareSearchService = this.softwareSearchService;
    }

    public static EntitySearchFactory getInstance(){
        return instance;
    }

    public EntitySearchService create(EntityType type){
        EntitySearchFactory indexFactory = EntitySearchFactory.getInstance();
        if(type.equals(EntityType.ARTICLE))
            return indexFactory.articleSearchService;
        if(type.equals(EntityType.QUESTION))
            return indexFactory.questionSearchService;
        if(type.equals(EntityType.TAG))
            return indexFactory.tagSearchService;
        if(type.equals(EntityType.ANSWER))
            return indexFactory.answerSearchService;
        if(type.equals(EntityType.COLUMN))
            return indexFactory.columnSearchService;
        if(type.equals(EntityType.USER))
            return indexFactory.userSearchService;
        if(type.equals(EntityType.SOFTWARE))
            return indexFactory.softwareSearchService;
        return null;
    }
}
