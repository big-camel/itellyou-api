package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionStarDao;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.event.QuestionEvent;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.question.QuestionStarDetailModel;
import com.itellyou.model.question.QuestionStarModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.service.common.StarService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.QuestionInfoService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "question")
@Service
public class QuestionStarServiceImpl implements StarService<QuestionStarModel> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionStarDao starDao;
    private final QuestionInfoService infoService;
    private final QuestionSearchService searchService;

    private final OperationalPublisher operationalPublisher;

    @Autowired
    public QuestionStarServiceImpl(QuestionStarDao starDao, QuestionInfoService infoService, QuestionSearchService searchService, OperationalPublisher operationalPublisher){
        this.starDao = starDao;
        this.infoService = infoService;
        this.searchService = searchService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#model.questionId")
    public int insert(QuestionStarModel model) throws Exception {
        QuestionInfoModel infoModel = searchService.findById(model.getQuestionId());
        try{
            if(infoModel == null) throw new Exception("错误的问题ID");
            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入关注记录失败");
            result = infoService.updateStarCountById(model.getQuestionId(),1);
            if(result != 1) throw new Exception("更新关注数失败");
            operationalPublisher.publish(new QuestionEvent(this, EntityAction.FOLLOW,model.getQuestionId(),infoModel.getCreatedUserId(),model.getCreatedUserId(), DateUtils.getTimestamp(),model.getCreatedIp()));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        return 1;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#questionId")
    public int delete(Long questionId, Long userId,Long ip) throws Exception {
        QuestionInfoModel infoModel = searchService.findById(questionId);
        try{
            if(infoModel == null) throw new Exception("错误的问题ID");
            int result = starDao.delete(questionId,userId);
            if(result != 1) throw new Exception("删除关注记录失败");
            result = infoService.updateStarCountById(questionId,-1);
            if(result != 1) throw new Exception("更新关注数失败");
            operationalPublisher.publish(new QuestionEvent(this, EntityAction.UNFOLLOW,questionId,infoModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));

        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
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
