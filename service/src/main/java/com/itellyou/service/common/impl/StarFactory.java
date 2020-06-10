package com.itellyou.service.common.impl;

import com.itellyou.model.sys.EntityType;
import com.itellyou.service.article.impl.ArticleStarServiceImpl;
import com.itellyou.service.column.impl.ColumnStarServiceImpl;
import com.itellyou.service.common.StarService;
import com.itellyou.service.question.impl.QuestionAnswerStarServiceImpl;
import com.itellyou.service.question.impl.QuestionStarServiceImpl;
import com.itellyou.service.tag.impl.TagStarServiceImpl;
import com.itellyou.service.user.star.impl.UserStarServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class StarFactory {

    private ArticleStarServiceImpl articleStarService;
    private QuestionStarServiceImpl questionStarService;
    private QuestionAnswerStarServiceImpl answerStarService;
    private TagStarServiceImpl tagStarService;
    private ColumnStarServiceImpl columnStarService;
    private UserStarServiceImpl userStarService;

    private static StarFactory instance = new StarFactory();

    public StarFactory(){}

    @Autowired
    public StarFactory(ArticleStarServiceImpl articleStarService, QuestionStarServiceImpl questionStarService, QuestionAnswerStarServiceImpl answerStarService, TagStarServiceImpl tagStarService, ColumnStarServiceImpl columnStarService, UserStarServiceImpl userStarService) {
        this.articleStarService = articleStarService;
        this.questionStarService = questionStarService;
        this.answerStarService = answerStarService;
        this.tagStarService = tagStarService;
        this.columnStarService = columnStarService;
        this.userStarService = userStarService;
    }

    @PostConstruct
    public void init(){
        instance = this;
        instance.articleStarService = this.articleStarService;
        instance.questionStarService = this.questionStarService;
        instance.answerStarService = this.answerStarService;
        instance.tagStarService = this.tagStarService;
        instance.columnStarService = this.columnStarService;
        instance.userStarService = this.userStarService;
    }

    public static StarFactory getInstance(){
        return instance;
    }

    public static StarService create(EntityType type){
        StarFactory starFactory = StarFactory.getInstance();
        if(type.equals(EntityType.ARTICLE))
            return starFactory.articleStarService;
        if(type.equals(EntityType.QUESTION))
            return starFactory.questionStarService;
        if(type.equals(EntityType.TAG))
            return starFactory.tagStarService;
        if(type.equals(EntityType.ANSWER))
            return starFactory.answerStarService;
        if(type.equals(EntityType.COLUMN))
            return starFactory.columnStarService;
        if(type.equals(EntityType.USER))
            return starFactory.userStarService;
        return null;
    }
}
