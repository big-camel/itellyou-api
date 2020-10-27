package com.itellyou.service.sys.impl;

import com.itellyou.service.sys.SysIncomeManagerService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SysIncomeManagerServiceImpl implements SysIncomeManagerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Scheduler scheduler;

    private final String identityKey = "sysIncomeManager";

    public SysIncomeManagerServiceImpl(Scheduler scheduler) {
        this.scheduler = scheduler;

        JobDetail job = JobBuilder.newJob(SysIncomeJobServiceImpl.class).withIdentity(identityKey).storeDurably().build();

        // 每月1日早上8点运行
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 8 1 * ?");

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
}
