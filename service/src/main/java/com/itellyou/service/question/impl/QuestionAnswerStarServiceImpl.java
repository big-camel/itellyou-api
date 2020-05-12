package com.itellyou.service.question.impl;

import com.itellyou.dao.question.QuestionAnswerStarDao;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.event.AnswerEvent;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerStarDetailModel;
import com.itellyou.model.question.QuestionAnswerStarModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.service.common.StarService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionAnswerService;
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

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "question_answer")
@Service
public class QuestionAnswerStarServiceImpl implements StarService<QuestionAnswerStarModel> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final QuestionAnswerStarDao starDao;
    private final QuestionAnswerService infoService;
    private final QuestionAnswerSearchService answerSearchService;
    private final UserInfoService userService;

    private final OperationalPublisher operationalPublisher;

    @Autowired
    public QuestionAnswerStarServiceImpl(QuestionAnswerStarDao starDao, QuestionAnswerService infoService, QuestionAnswerSearchService answerSearchService, UserInfoService userService, OperationalPublisher operationalPublisher){
        this.starDao = starDao;
        this.infoService = infoService;
        this.answerSearchService = answerSearchService;
        this.userService = userService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#model.answerId")
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
            operationalPublisher.publish(new AnswerEvent(this, EntityAction.FOLLOW,model.getAnswerId(),infoModel.getCreatedUserId(),model.getCreatedUserId(), DateUtils.getTimestamp(),model.getCreatedIp()));

        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        return 1;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#answerId")
    public int delete(Long answerId, Long userId,Long ip) throws Exception {
        QuestionAnswerModel infoModel = answerSearchService.findById(answerId);
        try{
            if(infoModel == null) throw new Exception("错误的回答ID");
            int result = starDao.delete(answerId,userId);
            if(result != 1) throw new Exception("删除收藏记录失败");
            result = infoService.updateStarCountById(answerId,-1);
            if(result != 1) throw new Exception("更新收藏数失败");
            result = userService.updateCollectionCount(userId,-1);
            if(result != 1) throw new Exception("更新用户收藏数失败");
            operationalPublisher.publish(new AnswerEvent(this, EntityAction.UNFOLLOW,infoModel.getId(),infoModel.getCreatedUserId(),infoModel.getCreatedUserId(), DateUtils.getTimestamp(),infoModel.getCreatedIp()));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
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
