package com.itellyou.service.common.impl;

import com.itellyou.model.common.DataUpdateQueueModel;
import com.itellyou.model.common.DataUpdateStepModel;
import com.itellyou.service.common.DataUpdateManageService;
import com.itellyou.service.common.DataUpdateQueueService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;

@Service
public class DataUpdateManagerServiceImpl implements DataUpdateManageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Scheduler scheduler;
    //达到最大多少自动执行任务
    private final int maxCount = 10000;

    private final DataUpdateQueueService queueService;

    private final String identityKey = "dataupdateManager";

    public DataUpdateManagerServiceImpl(Scheduler scheduler, DataUpdateQueueService queueService) {
        this.scheduler = scheduler;
        this.queueService = queueService;
        JobDetail job = JobBuilder.newJob(DataUpdateJobServiceImpl.class).withIdentity(identityKey).storeDurably().build();
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
    public <T extends DataUpdateStepModel> void put(DataUpdateQueueModel<T> model) {
        try {
            queueService.put(model);
            autoRun();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public <T extends DataUpdateStepModel> void put(DataUpdateQueueModel<T> model, BiConsumer<T, T> cumulative) {
        try {
            queueService.put(model,cumulative);
            autoRun();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public <T extends DataUpdateStepModel> void cumulative(T stepModel, T model) {
        queueService.cumulative(stepModel,model);
    }

    private void autoRun(){
        try {
            if(queueService.size() > maxCount){
                JobKey jobKey = new JobKey(identityKey);
                scheduler.triggerJob(jobKey);
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }
}
