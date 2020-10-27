package com.itellyou.service.statistics.impl;

import com.itellyou.model.common.DataUpdateStepModel;
import com.itellyou.model.statistics.StatisticsInfoModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.common.DataUpdateQueueService;
import com.itellyou.service.statistics.StatisticsInfoService;
import com.itellyou.util.DateUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 定时统计工作类，将统计队列写入数据库
 */
public class StatisticsJobServiceImpl extends QuartzJobBean {
    //日志
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //每次写入数据库最大条数
    private final int stepMax = 1000;
    //队列服务
    private final DataUpdateQueueService queueService;
    //基本服务
    private final StatisticsInfoService infoService;

    private final TransactionTemplate transactionTemplate;

    public StatisticsJobServiceImpl(DataUpdateQueueService queueService, StatisticsInfoService infoService, TransactionTemplate transactionTemplate) {
        this.queueService = queueService;
        this.infoService = infoService;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {

        String cacheKey = jobExecutionContext.getJobDetail().getJobDataMap().get("cacheKey").toString();
        //取出队列并清除缓存队列
        Map<Long, Map<Long, Map<EntityType, Collection<DataUpdateStepModel>>>> queueMap = queueService.reset(cacheKey);
        try {
            List<StatisticsInfoModel> list = new ArrayList<>();
            queueMap.forEach((userId,childes) -> {
                childes.forEach((date,values) -> {
                    values.forEach((type,models) -> {
                        models.forEach(stepModel -> {
                            StatisticsInfoModel infoModel = new StatisticsInfoModel();
                            infoModel.setUserId(userId);
                            infoModel.setDate(DateUtils.toLocalDate(date));
                            infoModel.setDataType(type);
                            infoModel.setDataKey(stepModel.getId());
                            infoModel.setViewCount(stepModel.getViewStep());
                            infoModel.setCommentCount(stepModel.getCommentStep() < 0 ? 0 : stepModel.getCommentStep());
                            infoModel.setSupportCount(stepModel.getSupportStep() < 0 ? 0 : stepModel.getSupportStep());
                            infoModel.setOpposeCount(stepModel.getOpposeStep() < 0 ? 0 : stepModel.getOpposeStep());
                            infoModel.setStarCount(stepModel.getStarStep() < 0 ? 0 : stepModel.getStarStep());
                            infoModel.setCreatedUserId(0l);
                            infoModel.setCreatedTime(DateUtils.toLocalDateTime());
                            infoModel.setCreatedIp(0l);
                            infoModel.setUpdatedUserId(0l);
                            infoModel.setUpdatedIp(0l);
                            infoModel.setUpdatedTime(DateUtils.toLocalDateTime());
                            list.add(infoModel);
                        });
                    });
                });
            });

            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        //批量添加/更新到数据库
                        queueService.submit(list,stepMax,data -> infoService.insertOrUpdate(data.toArray(new StatisticsInfoModel[data.size()])));
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
