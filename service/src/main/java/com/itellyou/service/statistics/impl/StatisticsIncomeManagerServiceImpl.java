package com.itellyou.service.statistics.impl;

import com.itellyou.model.statistics.StatisticsIncomeQueueModel;
import com.itellyou.model.statistics.StatisticsIncomeStepModel;
import com.itellyou.service.statistics.StatisticsIncomeManageService;
import com.itellyou.service.statistics.StatisticsIncomeQueueService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;

@Service
public class StatisticsIncomeManagerServiceImpl implements StatisticsIncomeManageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Scheduler scheduler;
    //达到最大多少自动执行任务
    private final int maxCount = 1000;

    private final StatisticsIncomeQueueService queueService;

    private final String identityKey = "statisticsIncomeManager";

    private final String cacheKey = "income";

    public StatisticsIncomeManagerServiceImpl(Scheduler scheduler, StatisticsIncomeQueueService queueService) {
        this.scheduler = scheduler;
        this.queueService = queueService;
        JobDetail job = JobBuilder.newJob(StatisticsIncomeJobServiceImpl.class).withIdentity(identityKey).usingJobData("cacheKey",cacheKey).storeDurably().build();

        // 每1小时运行统计
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 0/1 * * ?");

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
    public <T extends StatisticsIncomeStepModel> void put(StatisticsIncomeQueueModel<T> model) {
        try {
            queueService.put(cacheKey,model);
            autoRun();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public <T extends StatisticsIncomeStepModel> void put(StatisticsIncomeQueueModel<T> model, BiConsumer<T, T> cumulative) {
        try {
            queueService.put(cacheKey,model,cumulative);
            autoRun();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public <T extends StatisticsIncomeStepModel> void cumulative(T stepModel, T model) {
        queueService.cumulative(stepModel,model);
    }

    @Override
    public void run() {
        try {
            JobKey jobKey = new JobKey(identityKey);
            scheduler.triggerJob(jobKey);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    private void autoRun(){
        try {
            if(queueService.size() > maxCount){
                run();
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }
}
