package com.itellyou.service.common.impl;

import com.itellyou.model.common.IndexQueueModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.common.IndexQueueService;
import com.itellyou.service.common.IndexService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class IndexJobServiceImpl extends QuartzJobBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IndexQueueService queueService;

    public IndexJobServiceImpl( IndexQueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        try {
            LinkedHashMap<EntityType, HashSet<IndexQueueModel>> queueMap = queueService.reset();
            if (queueMap.size() > 0) {

                Iterator<Map.Entry<EntityType, HashSet<IndexQueueModel>>> iterator = queueMap.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry entry = iterator.next();
                    EntityType type = (EntityType) entry.getKey();
                    HashSet<Long> updateIds = new HashSet<>();
                    HashSet<Long> deleteIds = new HashSet<>();
                    for (IndexQueueModel queueModel : (HashSet<IndexQueueModel>) entry.getValue()){
                        if(queueModel.isDelete()) deleteIds.add(queueModel.getId());
                        else updateIds.add(queueModel.getId());
                    }
                    IndexService indexService = IndexFactory.create(type);
                    if(updateIds.size() > 0){
                        indexService.updateIndex(updateIds);
                    }
                    if(deleteIds.size() > 0){
                        indexService.delete(deleteIds);
                    }
                }
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }
}
