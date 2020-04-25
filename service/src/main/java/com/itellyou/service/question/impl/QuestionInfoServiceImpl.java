package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionInfoDao;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.question.*;
import com.itellyou.model.reward.RewardType;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.view.ViewInfoModel;
import com.itellyou.service.question.QuestionIndexService;
import com.itellyou.service.question.QuestionInfoService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.service.question.QuestionVersionService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.service.view.ViewInfoService;
import com.itellyou.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Service
public class QuestionInfoServiceImpl implements QuestionInfoService {

    private final QuestionInfoDao questionInfoDao;

    private final QuestionVersionService versionService;

    private final ViewInfoService viewService;

    private final QuestionIndexService indexerService;

    private final QuestionSearchService questionSearchService;

    private final UserInfoService userInfoService;
    private final UserDraftService draftService;

    @Autowired
    public QuestionInfoServiceImpl(QuestionInfoDao questionInfoDao, QuestionVersionService versionService, ViewInfoService viewService, QuestionIndexService indexerService, QuestionSearchService questionSearchService, UserInfoService userInfoService, UserDraftService draftService){
        this.questionInfoDao = questionInfoDao;
        this.versionService = versionService;
        this.viewService = viewService;
        this.indexerService = indexerService;
        this.questionSearchService = questionSearchService;
        this.userInfoService = userInfoService;
        this.draftService = draftService;
    }

    @Override
    public int insert(QuestionInfoModel questionInfoModel) {
        return questionInfoDao.insert(questionInfoModel);
    }


    @Override
    @Transactional
    public int updateView(Long userId,Long id,Long ip,String os,String browser) {

        try{
            long prevTime = viewService.insertOrUpdate(userId,EntityType.QUESTION,id,ip,os,browser);

            if(DateUtils.getTimestamp() - prevTime > 3600){
                int result = questionInfoDao.updateView(id,1);
                if(result != 1){
                    throw new Exception("更新浏览次数失败");
                }
                indexerService.updateIndex(id);
            }
            indexerService.updateIndex(id);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    public int updateAnswers(Long id, Integer value) {
        int result = questionInfoDao.updateAnswers(id,value);
        return result;
    }

    @Override
    public int updateAdopt(Boolean isAdopted, Long adoptionId, Long id) {
        return questionInfoDao.updateAdopt(isAdopted,adoptionId,id);
    }

    @Override
    public int updateComments(Long id, Integer value) {
        return questionInfoDao.updateComments(id,value);
    }

    @Override
    public int updateStarCountById(Long id, Integer step) {
        return questionInfoDao.updateStarCountById(id,step);
    }

    @Override
    @Transactional
    public Long create(Long userId, String title, String content, String html, String description, RewardType rewardType, Double rewardValue, Double rewardAdd, List<TagInfoModel> tags, String remark, String save_type, Long ip) throws Exception {
        try{
            QuestionInfoModel infoModel = new QuestionInfoModel();
            infoModel.setDraft(0);
            infoModel.setCreatedIp(ip);
            infoModel.setCreatedTime(DateUtils.getTimestamp());
            infoModel.setCreatedUserId(userId);
            int resultRows = insert(infoModel);
            if(resultRows != 1)
                throw new Exception("写入提问失败");
            QuestionVersionModel versionModel = versionService.addVersion(infoModel.getId(),userId,title,content,html,description,rewardType,rewardValue,rewardAdd,tags,remark,1,save_type,ip,false,true);
            if(versionModel == null)
                throw new Exception("写入版本失败");
            return infoModel.getId();
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    @Transactional
    public int updateDeleted(boolean deleted, Long id,Long userId) {
        try {
            QuestionInfoModel questionDetailModel = questionSearchService.findById(id);
            if(questionDetailModel == null) throw new Exception("未找到问题");
            if(!questionDetailModel.getCreatedUserId().equals(userId)) throw new Exception("无权限");
            int result = questionInfoDao.updateDeleted(deleted,id);
            if(result != 1)throw new Exception("删除失败");
            if(deleted){
                indexerService.delete(id);
                draftService.delete(userId, EntityType.ARTICLE,id);
            }else{
                indexerService.updateIndex(id);
            }
            userInfoService.updateArticleCount(userId,deleted ? -1 : 1);
            return result;
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }

    }

    @Override
    public int updateMetas(Long id, String cover) {
        return questionInfoDao.updateMetas(id,cover);
    }
}
