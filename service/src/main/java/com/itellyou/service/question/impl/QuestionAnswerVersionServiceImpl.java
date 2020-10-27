package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerVersionDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionAnswerVersionModel;
import com.itellyou.service.question.QuestionAnswerVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = CacheKeys.QUESTION_ANSWER_VERSION_KEY)
@Service
public class QuestionAnswerVersionServiceImpl implements QuestionAnswerVersionService {

    private final QuestionAnswerVersionDao versionDao;

    @Autowired
    public QuestionAnswerVersionServiceImpl(QuestionAnswerVersionDao versionDao){
        this.versionDao = versionDao;
    }

    @Override
    public int insert(QuestionAnswerVersionModel versionModel) {
        int id = versionDao.insert(versionModel);
        if(id < 1){
            return 0;
        }
        Integer version = versionDao.findVersionById(versionModel.getId());
        versionModel.setVersion(version);
        return id;
    }
}
