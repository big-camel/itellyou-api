package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerCommentVoteDao;
import com.itellyou.dao.question.QuestionAnswerVoteDao;
import com.itellyou.model.question.QuestionAnswerCommentVoteModel;
import com.itellyou.model.question.QuestionAnswerVoteModel;
import com.itellyou.service.question.QuestionAnswerCommentVoteService;
import com.itellyou.service.question.QuestionAnswerVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionAnswerCommentVoteServiceImpl implements QuestionAnswerCommentVoteService {
    private final QuestionAnswerCommentVoteDao voteDao;

    @Autowired
    public QuestionAnswerCommentVoteServiceImpl(QuestionAnswerCommentVoteDao voteDao){
        this.voteDao = voteDao;
    }

    @Override
    public int insert(QuestionAnswerCommentVoteModel voteModel) {
        return voteDao.insert(voteModel);
    }

    @Override
    public int deleteByCommentIdAndUserId(Long commentId, Long userId) {
        return voteDao.deleteByCommentIdAndUserId(commentId,userId);
    }

    @Override
    public QuestionAnswerCommentVoteModel findByCommentIdAndUserId(Long commentId, Long userId) {
        return voteDao.findByCommentIdAndUserId(commentId,userId);
    }
}
