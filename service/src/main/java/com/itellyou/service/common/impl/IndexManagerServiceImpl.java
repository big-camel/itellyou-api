package com.itellyou.service.common.impl;

import com.itellyou.model.common.IndexQueueModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.common.IndexManagerService;
import com.itellyou.service.common.IndexQueueService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class IndexManagerServiceImpl implements IndexManagerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Scheduler scheduler;

    private final int maxCount = 100;

    private final IndexQueueService queueService;

    public IndexManagerServiceImpl(Scheduler scheduler, IndexQueueService queueService) {
        this.scheduler = scheduler;
        this.queueService = queueService;
        JobDetail job = JobBuilder.newJob(IndexJobServiceImpl.class).withIdentity("indexManager").storeDurably().build();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/30 * * * ?");

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("indexManager")
            .withSchedule(cronScheduleBuilder)
            .build();

        try {
            scheduler.scheduleJob(job, trigger);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void put(IndexQueueModel model) {
        try {
            queueService.put(model);
            if(queueService.size() > maxCount){
                JobKey jobKey = new JobKey("indexManager");
                scheduler.triggerJob(jobKey);
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void put(EntityType type, HashSet<Long> ids) {
        for (Long id : ids){
            IndexQueueModel model = new IndexQueueModel(type,id);
            put(model);
        }
    }
}
