package com.itellyou.service.statistics.impl;

import com.itellyou.model.common.DataUpdateQueueModel;
import com.itellyou.service.common.DataUpdateQueueService;
import com.itellyou.service.statistics.StatisticsManageService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StatisticsManagerServiceImpl implements StatisticsManageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Scheduler scheduler;
    //达到最大多少自动执行任务
    private final int maxCount = 10000;

    private final DataUpdateQueueService queueService;

    private final String identityKey = "statisticsManager";

    private final String cacheKey = "statistics";

    public StatisticsManagerServiceImpl(Scheduler scheduler, DataUpdateQueueService queueService) {
        this.scheduler = scheduler;
        this.queueService = queueService;
        JobDetail job = JobBuilder.newJob(StatisticsJobServiceImpl.class).withIdentity(identityKey).usingJobData("cacheKey",cacheKey).storeDurably().build();

        // 每天0点运行统计
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 0 * * ?");

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(identityKey)
            .withSchedule(cronScheduleBuilder)
            .build();

        try {
            scheduler.scheduleJob(job, trigger);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void put(DataUpdateQueueModel model) {
        try {
            queueService.put(cacheKey,model);
            if(queueService.size() > maxCount){
                JobKey jobKey = new JobKey(identityKey);
                scheduler.triggerJob(jobKey);
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }
}
