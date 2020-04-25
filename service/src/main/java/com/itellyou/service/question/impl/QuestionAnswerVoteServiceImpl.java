package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerVoteDao;
import com.itellyou.model.question.QuestionAnswerVoteModel;
import com.itellyou.service.question.QuestionAnswerVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionAnswerVoteServiceImpl implements QuestionAnswerVoteService {
    private final QuestionAnswerVoteDao voteDao;

    @Autowired
    public QuestionAnswerVoteServiceImpl(QuestionAnswerVoteDao voteDao){
        this.voteDao = voteDao;
    }

    @Override
    public int insert(QuestionAnswerVoteModel voteModel) {
        return voteDao.insert(voteModel);
    }

    @Override
    public int deleteByAnswerIdAndUserId(Long answerId, Long userId) {
        return voteDao.deleteByAnswerIdAndUserId(answerId,userId);
    }

    @Override
    public QuestionAnswerVoteModel findByAnswerIdAndUserId(Long answerId, Long userId) {
        return voteDao.findByAnswerIdAndUserId(answerId,userId);
    }
}
