package com.itellyou.service.statistics.impl;

import com.itellyou.model.statistics.StatisticsIncomeModel;
import com.itellyou.model.statistics.StatisticsIncomeStepModel;
import com.itellyou.service.statistics.StatisticsIncomeQueueService;
import com.itellyou.service.statistics.StatisticsIncomeService;
import com.itellyou.util.DateUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定时统计工作类，将统计队列写入数据库
 */
public class StatisticsIncomeJobServiceImpl extends QuartzJobBean {
    //日志
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //每次写入数据库最大条数
    private final int stepMax = 1000;
    //队列服务
    private final StatisticsIncomeQueueService queueService;
    //基本服务
    private final StatisticsIncomeService incomeService;

    private final TransactionTemplate transactionTemplate;

    public StatisticsIncomeJobServiceImpl(StatisticsIncomeQueueService queueService, StatisticsIncomeService incomeService, TransactionTemplate transactionTemplate) {
        this.queueService = queueService;
        this.incomeService = incomeService;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {

        String cacheKey = jobExecutionContext.getJobDetail().getJobDataMap().get("cacheKey").toString();
        //取出队列并清除缓存队列
        Map<Long, Map<Long, StatisticsIncomeStepModel>> queueMap = queueService.reset(cacheKey);
        try {
            List<StatisticsIncomeModel> list = new ArrayList<>();
            queueMap.forEach((userId,values) -> {
                values.forEach((date,model) -> {
                    StatisticsIncomeModel incomeModel = new StatisticsIncomeModel();
                    incomeModel.setUserId(userId);
                    incomeModel.setDate(DateUtils.toLocalDate(date));
                    incomeModel.setTotalAmount(model.getTotalStep());
                    incomeModel.setTipAmount(model.getTipStep());
                    incomeModel.setRewardAmount(model.getRewardStep());
                    incomeModel.setSellAmount(model.getSellStep());
                    incomeModel.setSharingAmount(model.getSharingStep());
                    incomeModel.setOtherAmount(model.getOtherStep());
                    incomeModel.setCreatedUserId(0l);
                    incomeModel.setCreatedTime(DateUtils.toLocalDateTime());
                    incomeModel.setCreatedIp(0l);
                    incomeModel.setUpdatedUserId(0l);
                    incomeModel.setUpdatedIp(0l);
                    incomeModel.setUpdatedTime(DateUtils.toLocalDateTime());
                    list.add(incomeModel);
                });
            });

            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        //批量添加/更新到数据库
                        queueService.submit(list,stepMax,data -> incomeService.insertOrUpdate(data.toArray(new StatisticsIncomeModel[data.size()])));
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
