package com.itellyou.service.common.impl;

import com.itellyou.model.common.IndexQueueModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.common.IndexQueueService;
import com.itellyou.service.common.IndexService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class IndexJobServiceImpl extends QuartzJobBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IndexQueueService queueService;
    private final IndexFactory indexFactory;

    public IndexJobServiceImpl(IndexQueueService queueService, IndexFactory indexFactory) {
        this.queueService = queueService;
        this.indexFactory = indexFactory;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        try {
            LinkedHashMap<EntityType, Collection<IndexQueueModel>> queueMap = queueService.reset();
            queueMap.forEach((type,value) -> {
                Collection<Long> updateIds = new HashSet<>();
                Collection<Long> deleteIds = new HashSet<>();
                value.forEach(queueModel -> {
                    if(queueModel.isDelete()) deleteIds.add(queueModel.getId());
                    else updateIds.add(queueModel.getId());
                });
                IndexService indexService = indexFactory.create(type);
                if(updateIds.size() > 0){
                    indexService.updateIndex(updateIds);
                }
                if(deleteIds.size() > 0){
                    indexService.delete(deleteIds);
                }
            });
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }
}
