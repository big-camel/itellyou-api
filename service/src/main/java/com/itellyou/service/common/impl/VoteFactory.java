package com.itellyou.service.common.impl;

import com.itellyou.model.sys.EntityType;
import com.itellyou.service.article.impl.ArticleCommentVoteServiceImpl;
import com.itellyou.service.article.impl.ArticleVoteServiceImpl;
import com.itellyou.service.common.VoteService;
import com.itellyou.service.question.impl.*;
import com.itellyou.service.software.impl.SoftwareCommentVoteServiceImpl;
import com.itellyou.service.software.impl.SoftwareVoteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class VoteFactory {

    private ArticleCommentVoteServiceImpl articleCommentVoteService;
    private ArticleVoteServiceImpl articleVoteService;
    private QuestionCommentVoteServiceImpl questionCommentVoteService;
    private QuestionAnswerVoteServiceImpl questionAnswerVoteService;
    private QuestionAnswerCommentVoteServiceImpl questionAnswerCommentVoteService;
    private SoftwareCommentVoteServiceImpl softwareCommentVoteService;
    private SoftwareVoteServiceImpl softwareVoteService;

    private static VoteFactory instance = new VoteFactory();

    public VoteFactory(){}

    @Autowired
    public VoteFactory(ArticleCommentVoteServiceImpl articleCommentVoteService, ArticleVoteServiceImpl articleVoteService, QuestionCommentVoteServiceImpl questionCommentVoteService, QuestionAnswerVoteServiceImpl questionAnswerVoteService, QuestionAnswerCommentVoteServiceImpl questionAnswerCommentVoteService,SoftwareCommentVoteServiceImpl softwareCommentVoteService,SoftwareVoteServiceImpl softwareVoteService) {
        this.articleCommentVoteService = articleCommentVoteService;
        this.articleVoteService = articleVoteService;
        this.questionCommentVoteService = questionCommentVoteService;
        this.questionAnswerVoteService = questionAnswerVoteService;
        this.questionAnswerCommentVoteService = questionAnswerCommentVoteService;
        this.softwareCommentVoteService = softwareCommentVoteService;
        this.softwareVoteService = softwareVoteService;
    }

    @PostConstruct
    public void init(){
        instance = this;
        instance.articleCommentVoteService = articleCommentVoteService;
        instance.articleVoteService = articleVoteService;
        instance.questionCommentVoteService = questionCommentVoteService;
        instance.questionAnswerVoteService = questionAnswerVoteService;
        instance.questionAnswerCommentVoteService = questionAnswerCommentVoteService;
        instance.softwareCommentVoteService = softwareCommentVoteService;
        instance.softwareVoteService = softwareVoteService;
    }

    public static VoteFactory getInstance(){
        return instance;
    }

    public static VoteService create(EntityType type){
        VoteFactory voteFactory = VoteFactory.getInstance();
        if(type.equals(EntityType.ARTICLE))
            return voteFactory.articleVoteService;
        if(type.equals(EntityType.ARTICLE_COMMENT))
            return voteFactory.articleCommentVoteService;
        if(type.equals(EntityType.SOFTWARE))
            return voteFactory.softwareVoteService;
        if(type.equals(EntityType.SOFTWARE_COMMENT))
            return voteFactory.softwareCommentVoteService;
        if(type.equals(EntityType.QUESTION_COMMENT))
            return voteFactory.questionCommentVoteService;
        if(type.equals(EntityType.ANSWER))
            return voteFactory.questionAnswerVoteService;
        if(type.equals(EntityType.ANSWER_COMMENT))
            return voteFactory.questionAnswerCommentVoteService;
        return null;
    }
}
