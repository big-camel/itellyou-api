package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerCommentVoteDao;
import com.itellyou.dao.question.QuestionCommentVoteDao;
import com.itellyou.model.question.QuestionAnswerCommentVoteModel;
import com.itellyou.model.question.QuestionCommentVoteModel;
import com.itellyou.service.question.QuestionAnswerCommentVoteService;
import com.itellyou.service.question.QuestionCommentVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionCommentVoteServiceImpl implements QuestionCommentVoteService {
    private final QuestionCommentVoteDao voteDao;

    @Autowired
    public QuestionCommentVoteServiceImpl(QuestionCommentVoteDao voteDao){
        this.voteDao = voteDao;
    }

    @Override
    public int insert(QuestionCommentVoteModel voteModel) {
        return voteDao.insert(voteModel);
    }

    @Override
    public int deleteByCommentIdAndUserId(Long commentId, Long userId) {
        return voteDao.deleteByCommentIdAndUserId(commentId,userId);
    }

    @Override
    public QuestionCommentVoteModel findByCommentIdAndUserId(Long commentId, Long userId) {
        return voteDao.findByCommentIdAndUserId(commentId,userId);
    }
}
