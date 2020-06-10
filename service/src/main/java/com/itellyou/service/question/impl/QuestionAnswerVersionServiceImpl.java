package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerDao;
import com.itellyou.dao.question.QuestionAnswerVersionDao;
import com.itellyou.model.question.QuestionAnswerVersionModel;
import com.itellyou.service.question.QuestionAnswerVersionSearchService;
import com.itellyou.service.question.QuestionAnswerVersionService;
import com.itellyou.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = "question_answer_version")
@Service
public class QuestionAnswerVersionServiceImpl implements QuestionAnswerVersionService {

    private final QuestionAnswerVersionDao versionDao;
    private final QuestionAnswerDao answerDao;
    private final QuestionAnswerVersionSearchService versionSearchService;

    @Autowired
    public QuestionAnswerVersionServiceImpl(QuestionAnswerVersionDao versionDao, QuestionAnswerDao answerDao, QuestionAnswerVersionSearchService versionSearchService){
        this.versionDao = versionDao;
        this.answerDao = answerDao;
        this.versionSearchService = versionSearchService;
    }

    @Override
    public int insert(QuestionAnswerVersionModel versionModel) {
        int rows = versionDao.insert(versionModel);
        if(rows != 1){
            return 0;
        }
        Integer version = versionSearchService.findVersionById(versionModel.getId());
        versionModel.setVersion(version);
        return 1;
    }

    @Override
    public int updateVersion(Long id, Integer version, Long ip, Long user) {
        return updateVersion(id,version,false,null,null,ip,user);
    }

    @Override
    public int updateVersion(Long id, Integer version, Boolean isPublished, Boolean isDisabled,Boolean isDeleted, Long ip, Long user) {
        return updateVersion(id,version,null,isPublished,isDisabled,isDeleted, DateUtils.getTimestamp(),ip,user);
    }

    @Override
    public int updateVersion(Long id, Integer version, Integer draft, Boolean isPublished, Boolean isDisabled,Boolean isDeleted, Long time, Long ip, Long user) {
        return answerDao.updateVersion(id,version,draft,isPublished,isDisabled,isDeleted,time,ip,user);
    }

    @Override
    @Transactional
    public int updateVersion(QuestionAnswerVersionModel versionModel) {
        try{
            int result = insert(versionModel);
            if(result != 1)
            {
                throw new Exception("写入版本失败");
            }
            result = updateVersion(versionModel.getAnswerId(),versionModel.getVersion(),versionModel.getVersion(),versionModel.isPublished(),null,null,versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
            if(result != 1)
            {
                throw new Exception("更新版本号失败");
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    public int updateDraft(Long id, Integer version, Boolean isPublished, Boolean isDisabled,Boolean isDeleted, Long time, Long ip, Long user) {
        // 更新草稿版本不更新时间/ip/和用户编号
        return updateVersion(id,null,version,isPublished,isDisabled,isDeleted,null,null,null);
    }

    @Override
    public int updateDraft(Long id, Integer version, Long time, Long ip, Long user) {
        return updateDraft(id,version,null,null,null,time,ip,user);
    }

    @Override
    @Transactional
    public int updateDraft(QuestionAnswerVersionModel versionModel) {
        try{
            int result = insert(versionModel);
            if(result != 1)
            {
                throw new Exception("写入版本失败");
            }
            result = updateDraft(versionModel.getAnswerId(),versionModel.getVersion(),null,null,null,versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
            if(result != 1)
            {
                throw new Exception("更新草稿版本号失败");
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }
}
