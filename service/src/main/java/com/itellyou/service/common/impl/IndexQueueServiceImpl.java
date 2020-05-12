package com.itellyou.service.common.impl;

import com.itellyou.model.common.IndexQueueModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.common.IndexQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IndexQueueServiceImpl implements IndexQueueService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private LinkedHashMap<EntityType, HashSet<IndexQueueModel>> taskMap = new LinkedHashMap<>();

    public void put(IndexQueueModel queueModel) {
        try {
            synchronized (taskMap){
                if(!taskMap.containsKey(queueModel.getType())){
                    taskMap.put(queueModel.getType(),new LinkedHashSet<>());
                }
                if(taskMap.containsKey(queueModel.getType()) && !taskMap.get(queueModel.getType()).contains(queueModel.getId())){
                    taskMap.get(queueModel.getType()).add(queueModel);
                }
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    public int size(EntityType type){
        if(!taskMap.containsKey(type)) return 0;
        return taskMap.get(type).size();
    }

    @Override
    public int size() {
        Iterator<Map.Entry<EntityType, HashSet<IndexQueueModel>>> iterator= taskMap.entrySet().iterator();
        int size = 0;
        while(iterator.hasNext())
        {
            Map.Entry entry = iterator.next();
            size += ((HashSet<IndexQueueModel>)entry.getValue()).size();
        }
        return size;
    }

    @Override
    public LinkedHashMap<EntityType, HashSet<IndexQueueModel>> reset(){
        try {
            synchronized (taskMap) {
                LinkedHashMap<EntityType, HashSet<IndexQueueModel>> newMap = new LinkedHashMap<>();
                newMap.putAll(taskMap);
                taskMap.clear();
                return newMap;
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }
}
