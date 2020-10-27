package com.itellyou.service.common.impl;

import com.itellyou.model.common.DataUpdateStepModel;
import com.itellyou.model.question.QuestionUpdateStepModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.article.ArticleInfoService;
import com.itellyou.service.common.DataUpdateQueueService;
import com.itellyou.service.question.QuestionAnswerService;
import com.itellyou.service.question.QuestionInfoService;
import com.itellyou.service.software.SoftwareInfoService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

/**
 * 定时统计工作类，将统计队列写入数据库
 */
public class DataUpdateJobServiceImpl extends QuartzJobBean {
    //日志
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //每次写入数据库最大条数
    private final int stepMax = 1000;
    //队列服务
    private final DataUpdateQueueService queueService;
    //基本服务
    private final ArticleInfoService articleInfoService;
    private final QuestionAnswerService answerService;
    private final SoftwareInfoService softwareInfoService;
    private final QuestionInfoService questionService;
    private final TransactionTemplate transactionTemplate;

    public DataUpdateJobServiceImpl(DataUpdateQueueService queueService, ArticleInfoService articleInfoService, QuestionAnswerService answerService, SoftwareInfoService softwareInfoService, QuestionInfoService questionService, TransactionTemplate transactionTemplate) {
        this.queueService = queueService;
        this.articleInfoService = articleInfoService;
        this.answerService = answerService;
        this.softwareInfoService = softwareInfoService;
        this.questionService = questionService;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        //取出队列并清除缓存队列，统计包含昨日以前的数据
        Map<Long, Map<Long, Map<EntityType, Collection<DataUpdateStepModel>>>> queueMap = queueService.reset();
        //按照实体类型分组统计
        Map<EntityType, List<DataUpdateStepModel>> stepMap = new HashMap<>();
        try {
            queueMap.forEach((userId,childes) -> {
                childes.forEach((date,values) -> {
                    values.forEach((type,models) -> {
                        models.forEach(stepModel -> {
                            Collection<DataUpdateStepModel> stepModels = stepMap.computeIfAbsent(type,key -> new ArrayList<>());
                            Optional<DataUpdateStepModel> stepModelOptional = stepModels.stream().filter(model -> model.getId().equals(stepModel.getId())).findFirst();
                            stepModelOptional.ifPresent(model -> {
                                queueService.cumulative(model,stepModel);
                                if(type.equals(EntityType.QUESTION)){
                                    QuestionUpdateStepModel questionStepModel = (QuestionUpdateStepModel)model;
                                    QuestionUpdateStepModel questionStepModel1 = (QuestionUpdateStepModel)stepModel;
                                    questionStepModel.setAnswerStep(questionStepModel.getAnswerStep() + questionStepModel1.getAnswerStep());
                                }
                            });
                            if(!stepModelOptional.isPresent()){
                                stepModels.add(stepModel);
                            }
                        });
                    });
                });
            });
            //事务执行
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        //批量更新文章的额外计数
                        queueService.submit(stepMap.get(EntityType.ARTICLE),stepMax,data -> articleInfoService.addStep(data.toArray(new DataUpdateStepModel[data.size()])));
                        //批量更新回答的额外计数
                        queueService.submit(stepMap.get(EntityType.ANSWER),stepMax,data -> answerService.addStep(data.toArray(new DataUpdateStepModel[data.size()])));
                        //批量更新软件的额外计数
                        queueService.submit(stepMap.get(EntityType.SOFTWARE),stepMax,data -> softwareInfoService.addStep(data.toArray(new DataUpdateStepModel[data.size()])));
                        //批量更新问题的额外计数
                        queueService.submit(stepMap.get(EntityType.QUESTION),stepMax,data -> questionService.addStep(data.toArray(new QuestionUpdateStepModel[data.size()])));
                    }catch (Exception e){
                        logger.error(e.getLocalizedMessage());
                        status.setRollbackOnly();
                        queueService.save(queueMap);
                    }
                }
            });
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }


}
