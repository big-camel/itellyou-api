package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionStarDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.question.QuestionStarDetailModel;
import com.itellyou.model.question.QuestionStarModel;
import com.itellyou.model.user.UserOperationalAction;
import com.itellyou.model.user.UserOperationalModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.question.QuestionIndexService;
import com.itellyou.service.question.QuestionInfoService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.service.question.QuestionStarService;
import com.itellyou.service.user.UserOperationalService;
import com.itellyou.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

@Service
public class QuestionStarServiceImpl implements QuestionStarService {

    private final QuestionStarDao starDao;
    private final QuestionInfoService infoService;
    private final QuestionSearchService searchService;
    private final QuestionIndexService indexService;
    private final UserOperationalService operationalService;

    @Autowired
    public QuestionStarServiceImpl(QuestionStarDao starDao,QuestionInfoService infoService,QuestionSearchService searchService,QuestionIndexService indexService,UserOperationalService operationalService){
        this.starDao = starDao;
        this.infoService = infoService;
        this.searchService = searchService;
        this.indexService = indexService;
        this.operationalService = operationalService;
    }

    @Override
    @Transactional
    public int insert(QuestionStarModel model) throws Exception {
        QuestionInfoModel infoModel = searchService.findById(model.getQuestionId());
        try{
            if(infoModel == null) throw new Exception("错误的问题ID");
            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入关注记录失败");
            result = infoService.updateStarCountById(model.getQuestionId(),1);
            if(result != 1) throw new Exception("更新关注数失败");
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        indexService.updateIndex(model.getQuestionId());
        operationalService.insertAsync(new UserOperationalModel(UserOperationalAction.FOLLOW, EntityType.QUESTION,model.getQuestionId(),infoModel.getCreatedUserId(),model.getCreatedUserId(), DateUtils.getTimestamp(),model.getCreatedIp()));
        return 1;
    }

    @Override
    @Transactional
    public int delete(Long questionId, Long userId) throws Exception {
        try{
            int result = starDao.delete(questionId,userId);
            if(result != 1) throw new Exception("删除关注记录失败");
            result = infoService.updateStarCountById(questionId,-1);
            if(result != 1) throw new Exception("更新关注数失败");
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        indexService.updateIndex(questionId);
        operationalService.deleteByTargetIdAsync(UserOperationalAction.FOLLOW, EntityType.QUESTION,userId,questionId);
        return 1;
    }

    @Override
    public List<QuestionStarDetailModel> search(Long questionId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return starDao.search(questionId,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(Long questionId, Long userId, Long beginTime, Long endTime, Long ip) {
        return starDao.count(questionId,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<QuestionStarDetailModel> page(Long questionId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<QuestionStarDetailModel> data = search(questionId,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(questionId,userId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
