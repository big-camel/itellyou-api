package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleVoteDao;
import com.itellyou.model.article.ArticleVoteModel;
import com.itellyou.service.article.ArticleVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleVoteServiceImpl implements ArticleVoteService {
    private final ArticleVoteDao voteDao;

    @Autowired
    public ArticleVoteServiceImpl(ArticleVoteDao voteDao){
        this.voteDao = voteDao;
    }

    @Override
    public int insert(ArticleVoteModel voteModel) {
        return voteDao.insert(voteModel);
    }

    @Override
    public int deleteByArticleIdAndUserId(Long articleId, Long userId) {
        return voteDao.deleteByArticleIdAndUserId(articleId,userId);
    }

    @Override
    public ArticleVoteModel findByArticleIdAndUserId(Long articleId, Long userId) {
        return voteDao.findByArticleIdAndUserId(articleId,userId);
    }
}
