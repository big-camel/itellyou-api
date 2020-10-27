package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionInfoDao;
import com.itellyou.dao.question.QuestionVersionDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.question.QuestionVersionModel;
import com.itellyou.service.question.QuestionTagService;
import com.itellyou.service.question.QuestionVersionService;
import com.itellyou.service.question.QuestionVersionTagService;
import com.itellyou.service.user.bank.UserBankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = CacheKeys.QUESTION_VERSION_KEY)
@Service
public class QuestionVersionServiceImpl implements QuestionVersionService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionVersionDao versionDao;
    private final QuestionInfoDao infoDao;
    private final UserBankService bankService;
    private final QuestionVersionTagService questionVersionTagService;

    @Autowired
    public QuestionVersionServiceImpl(QuestionVersionDao questionVersionDao, QuestionInfoDao infoDao, UserBankService bankService, QuestionTagService questionTagService, QuestionVersionTagService questionVersionTagService){
        this.versionDao = questionVersionDao;
        this.infoDao = infoDao;
        this.bankService = bankService;
        this.questionVersionTagService = questionVersionTagService;
    }

    @Override
    public int insert(QuestionVersionModel questionVersionModel) {
        try{
            int id = versionDao.insert(questionVersionModel);
            if(id < 1){
                throw new Exception("写入版本失败");
            }
            Integer version = versionDao.findVersionById(questionVersionModel.getId());
            questionVersionModel.setVersion(version);
            return id;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}
