package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerStarDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerStarDetailModel;
import com.itellyou.model.question.QuestionAnswerStarModel;
import com.itellyou.model.user.UserOperationalAction;
import com.itellyou.model.user.UserOperationalModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.question.QuestionAnswerIndexService;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionAnswerService;
import com.itellyou.service.question.QuestionAnswerStarService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.service.user.UserOperationalService;
import com.itellyou.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

@Service
public class QuestionAnswerStarServiceImpl implements QuestionAnswerStarService {

    private final QuestionAnswerStarDao starDao;
    private final QuestionAnswerService infoService;
    private final QuestionAnswerSearchService answerSearchService;
    private final QuestionAnswerIndexService answerIndexService;
    private final UserOperationalService operationalService;
    private final UserInfoService userService;

    @Autowired
    public QuestionAnswerStarServiceImpl(QuestionAnswerStarDao starDao, QuestionAnswerService infoService,QuestionAnswerSearchService answerSearchService,QuestionAnswerIndexService answerIndexService, UserOperationalService operationalService,UserInfoService userService){
        this.starDao = starDao;
        this.infoService = infoService;
        this.answerSearchService = answerSearchService;
        this.answerIndexService = answerIndexService;
        this.operationalService = operationalService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public int insert(QuestionAnswerStarModel model) throws Exception {
        QuestionAnswerModel infoModel = answerSearchService.findById(model.getAnswerId());
        try{
            if(infoModel == null) throw new Exception("错误的回答ID");
            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入收藏记录失败");
            result = infoService.updateStarCountById(model.getAnswerId(),1);
            if(result != 1) throw new Exception("更新收藏数失败");
            result = userService.updateCollectionCount(model.getCreatedUserId(),1);
            if(result != 1) throw new Exception("更新用户收藏数失败");
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        answerIndexService.updateIndex(model.getAnswerId());
        operationalService.insertAsync(new UserOperationalModel(UserOperationalAction.FOLLOW, EntityType.ANSWER,model.getAnswerId(),infoModel.getCreatedUserId(),model.getCreatedUserId(), DateUtils.getTimestamp(),model.getCreatedIp()));
        return 1;
    }

    @Override
    @Transactional
    public int delete(Long answerId, Long userId) throws Exception {
        try{
            int result = starDao.delete(answerId,userId);
            if(result != 1) throw new Exception("删除收藏记录失败");
            result = infoService.updateStarCountById(answerId,-1);
            if(result != 1) throw new Exception("更新收藏数失败");
            result = userService.updateCollectionCount(userId,-1);
            if(result != 1) throw new Exception("更新用户收藏数失败");
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        answerIndexService.updateIndex(answerId);
        operationalService.deleteByTargetIdAsync(UserOperationalAction.FOLLOW, EntityType.ANSWER,userId,answerId);
        return 1;
    }

    @Override
    public List<QuestionAnswerStarDetailModel> search(Long answerId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return starDao.search(answerId,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(Long answerId, Long userId, Long beginTime, Long endTime, Long ip) {
        return starDao.count(answerId,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<QuestionAnswerStarDetailModel> page(Long answerId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<QuestionAnswerStarDetailModel> data = search(answerId,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(answerId,userId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
