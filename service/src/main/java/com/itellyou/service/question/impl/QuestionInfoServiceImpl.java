package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionInfoDao;
import com.itellyou.model.event.QuestionEvent;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.RewardType;
import com.itellyou.service.common.ViewService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.QuestionInfoService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.service.question.QuestionSingleService;
import com.itellyou.service.question.QuestionVersionService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = "question")
@Service
public class QuestionInfoServiceImpl implements QuestionInfoService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionInfoDao questionInfoDao;
    private final QuestionVersionService versionService;
    private final ViewService viewService;
    private final QuestionSearchService questionSearchService;
    private final QuestionSingleService questionSingleService;
    private final UserInfoService userInfoService;
    private final UserDraftService draftService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public QuestionInfoServiceImpl(QuestionInfoDao questionInfoDao, QuestionVersionService versionService, ViewService viewService, QuestionSearchService questionSearchService, QuestionSingleService questionSingleService, UserInfoService userInfoService, UserDraftService draftService, OperationalPublisher operationalPublisher){
        this.questionInfoDao = questionInfoDao;
        this.versionService = versionService;
        this.viewService = viewService;
        this.questionSearchService = questionSearchService;
        this.questionSingleService = questionSingleService;
        this.userInfoService = userInfoService;
        this.draftService = draftService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    public int insert(QuestionInfoModel questionInfoModel) {
        return questionInfoDao.insert(questionInfoModel);
    }


    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public int updateView(Long userId,Long id,Long ip,String os,String browser) {

        try{
            QuestionInfoModel questionModel = questionSingleService.findById(id);
            if(questionModel == null) throw new Exception("错误的编号");
            long prevTime = viewService.insertOrUpdate(userId,EntityType.QUESTION,id,ip,os,browser);

            if(DateUtils.getTimestamp() - prevTime > 3600){
                int result = questionInfoDao.updateView(id,1);
                if(result != 1){
                    throw new Exception("更新浏览次数失败");
                }
                operationalPublisher.publish(new QuestionEvent(this, EntityAction.VIEW,id,questionModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateAnswers(Long id, Integer value) {
        int result = questionInfoDao.updateAnswers(id,value);
        return result;
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateAdopt(Boolean isAdopted, Long adoptionId, Long id) {
        return questionInfoDao.updateAdopt(isAdopted,adoptionId,id);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateComments(Long id, Integer value) {
        return questionInfoDao.updateComments(id,value);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateStarCountById(Long id, Integer step) {
        return questionInfoDao.updateStarCountById(id,step);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public int updateDeleted(boolean deleted, Long id,Long userId,Long ip) {
        try {
            QuestionInfoModel questionInfoModel = questionSingleService.findById(id);
            if(questionInfoModel == null) throw new Exception("未找到问题");
            if(!questionInfoModel.getCreatedUserId().equals(userId)) throw new Exception("无权限");
            int result = questionInfoDao.updateDeleted(deleted,id);
            if(result != 1)throw new Exception("删除失败");
            if(deleted){
                draftService.delete(userId, EntityType.ARTICLE,id);
            }
            userInfoService.updateArticleCount(userId,deleted ? -1 : 1);
            operationalPublisher.publish(new QuestionEvent(this,deleted ? EntityAction.DELETE : EntityAction.REVERT,
                    id,questionInfoModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
            return result;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }

    }

    @Override
    @CacheEvict(key = "#id")
    public int updateMetas(Long id, String cover) {
        return questionInfoDao.updateMetas(id,cover);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateInfo(Long id, String title, String description, RewardType rewardType, Double rewardAdd, Double rewardValue, Long time, Long ip, Long userId) {
        return questionInfoDao.updateInfo(id,title,description,rewardType,rewardAdd,rewardValue,time,ip,userId);
    }
}
