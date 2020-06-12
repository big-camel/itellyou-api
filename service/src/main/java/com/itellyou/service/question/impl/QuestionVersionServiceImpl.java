package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionInfoDao;
import com.itellyou.dao.question.QuestionVersionDao;
import com.itellyou.model.question.QuestionVersionModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.RewardType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankType;
import com.itellyou.service.question.QuestionTagService;
import com.itellyou.service.question.QuestionVersionService;
import com.itellyou.service.question.QuestionVersionTagService;
import com.itellyou.service.user.bank.UserBankService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

@CacheConfig(cacheNames = "question_version")
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
    @Transactional
    public int insert(QuestionVersionModel questionVersionModel) {
        try{
            int rows = versionDao.insert(questionVersionModel);
            if(rows != 1){
                throw new Exception("写入版本失败");
            }
            Integer version = versionDao.findVersionById(questionVersionModel.getId());
            questionVersionModel.setVersion(version);
            List<TagDetailModel> tags = questionVersionModel.getTags();
            if(tags != null && tags.size() > 0){
                HashSet<Long> tagIds = new LinkedHashSet<>();
                for (TagDetailModel tagDetailModel : tags){
                    tagIds.add(tagDetailModel.getId());
                }
                rows = questionVersionTagService.addAll(questionVersionModel.getId(),tagIds);
                if(rows != tags.size()){
                    throw new Exception("写入版本标签失败");
                }
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    @Transactional
    @Caching( evict = { @CacheEvict(key = "#versionModel.id"), @CacheEvict(key = "T(String).valueOf(#versionModel.questionId).concat('-').concat(#versionModel.version)")})
    public int update(QuestionVersionModel versionModel) {
        try{
            int rows = versionDao.update(versionModel);
            if(rows != 1){
                throw new Exception("更新版本失败");
            }
            if(versionModel.getVersion() == null || versionModel.getVersion() <= 0 ){
                Integer version = versionDao.findVersionById(versionModel.getId());
                versionModel.setVersion(version);
            }

            List<TagDetailModel> tags = versionModel.getTags();
            if(tags != null){
                questionVersionTagService.clear(versionModel.getId());
                if(tags.size() > 0){
                    HashSet<Long> tagIds = new LinkedHashSet<>();
                    for (TagDetailModel tagDetailModel : tags){
                        tagIds.add(tagDetailModel.getId());
                    }
                    rows = questionVersionTagService.addAll(versionModel.getId(),tagIds);
                    if(rows != tags.size()){
                        throw new Exception("更新版本标签失败");
                    }
                }
            }
            return 1;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }


    @Override
    public int updateVersion(Long questionId, Integer version, Long ip, Long user) {
        return updateVersion(questionId,version,false,ip,user);
    }

    @Override
    public int updateVersion(Long questionId, Integer version,Boolean isPublished, Long ip, Long user) {
        return updateVersion(questionId,version,null,isPublished, DateUtils.getTimestamp(),ip,user);
    }

    @Override
    @CacheEvict(value = "question",key = "#questionId")
    public int updateVersion(Long questionId, Integer version, Integer draft,Boolean isPublished, Long time, Long ip, Long user) {
        return infoDao.updateVersion(questionId,version,draft,isPublished,time,ip,user);
    }

    @Override
    @Transactional
    @Caching( evict = { @CacheEvict(key = "#versionModel.id"), @CacheEvict(key = "T(String).valueOf(#versionModel.questionId).concat('-').concat(#versionModel.version)")})
    public int updateVersion(QuestionVersionModel versionModel) {
        try{
            Long versionId = versionModel.getId();
            int result = versionId == null || versionId.equals(0l) ? insert(versionModel) : update(versionModel);
            if(result != 1)
            {
                throw new Exception("更新版本失败");
            }
            result = updateVersion(versionModel.getQuestionId(),versionModel.getVersion(),versionModel.getVersion(),versionModel.isPublished(),versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
            if(result != 1)
            {
                throw new Exception("更新版本号失败");
            }
            if(versionModel.isPublished() && versionModel.getRewardAdd() > 0 && !versionModel.getRewardType().equals(RewardType.DEFAULT)){
                UserBankLogModel logModel = bankService.update(-versionModel.getRewardAdd(), UserBankType.valueOf(versionModel.getRewardType().getValue())
                        ,EntityAction.PUBLISH,EntityType.ANSWER,versionModel.getQuestionId().toString(),versionModel.getCreatedUserId(),"问题悬赏",versionModel.getCreatedIp());
                if(logModel == null){
                    throw new Exception("赏金扣除失败");
                }
            }
            return result;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    public int updateDraft(Long questionId, Integer version,Boolean isPublished, Long time, Long ip, Long user) {
        return updateVersion(questionId,null,version,isPublished,time,ip,user);
    }

    @Override
    public int updateDraft(Long questionId, Integer version, Long time, Long ip, Long user) {
        return updateDraft(questionId,version,null,time,ip,user);
    }

    @Override
    @Transactional
    public int updateDraft(QuestionVersionModel versionModel) {
        try{
            int result = insert(versionModel);
            if(result != 1)
            {
                throw new Exception("写入版本失败");
            }
            result = updateDraft(versionModel.getQuestionId(),versionModel.getVersion(),versionModel.getCreatedTime(),versionModel.getCreatedIp(),versionModel.getCreatedUserId());
            if(result != 1)
            {
                throw new Exception("更新草稿版本号失败");
            }
            return result;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }
}
