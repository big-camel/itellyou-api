package com.itellyou.service.article.impl;

import com.itellyou.dao.article.ArticleCommentVoteDao;
import com.itellyou.model.article.ArticleCommentVoteModel;
import com.itellyou.service.article.ArticleCommentVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleCommentVoteServiceImpl implements ArticleCommentVoteService {
    private final ArticleCommentVoteDao voteDao;

    @Autowired
    public ArticleCommentVoteServiceImpl(ArticleCommentVoteDao voteDao){
        this.voteDao = voteDao;
    }

    @Override
    public int insert(ArticleCommentVoteModel voteModel) {
        return voteDao.insert(voteModel);
    }

    @Override
    public int deleteByCommentIdAndUserId(Long commentId, Long userId) {
        return voteDao.deleteByCommentIdAndUserId(commentId,userId);
    }

    @Override
    public ArticleCommentVoteModel findByCommentIdAndUserId(Long commentId, Long userId) {
        return voteDao.findByCommentIdAndUserId(commentId,userId);
    }
}
