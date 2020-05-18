package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerVersionDao;
import com.itellyou.model.question.QuestionAnswerVersionModel;
import com.itellyou.service.question.QuestionAnswerVersionSearchService;
import org.springframework.stereotype.Service;

@Service
public class QuestionAnswerVersionSearchServiceImpl implements QuestionAnswerVersionSearchService {

    private final QuestionAnswerVersionDao versionDao;

    public QuestionAnswerVersionSearchServiceImpl(QuestionAnswerVersionDao versionDao) {
        this.versionDao = versionDao;
    }

    @Override
    public QuestionAnswerVersionModel find(Long answerId, Integer version) {
        return versionDao.findByAnswerIdAndVersion(answerId,version);
    }
}
