package com.itellyou.service.common.impl;

import com.itellyou.model.common.IndexQueueModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.common.IndexQueueService;
import com.itellyou.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

@Service
public class IndexQueueServiceImpl implements IndexQueueService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String cacheKey = "default";

    public void put(IndexQueueModel queueModel) {
        try {
            synchronized (logger){
                LinkedHashMap<EntityType, Collection<IndexQueueModel>> taskMap = get();
                Collection<IndexQueueModel> queueModels = taskMap.computeIfAbsent(queueModel.getType(),key -> new LinkedHashSet());
                if(!queueModels.contains(queueModel.getId())){
                    queueModels.add(queueModel);
                }
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    public int size(EntityType type){
        LinkedHashMap<EntityType, Collection<IndexQueueModel>> taskMap = get();
        if(!taskMap.containsKey(type)) return 0;
        return taskMap.get(type).size();
    }

    @Override
    public int size() {
        return get().values().stream().mapToInt(Collection::size).sum();
    }

    @Override
    public LinkedHashMap<EntityType, Collection<IndexQueueModel>> get() {
        LinkedHashMap<EntityType, Collection<IndexQueueModel>> taskMap = RedisUtils.get(CacheKeys.INDEX_QUEUE_KEY,cacheKey,LinkedHashMap.class);
        if(taskMap == null) taskMap = new LinkedHashMap<>();
        return taskMap;
    }

    @Override
    public void save(LinkedHashMap<EntityType, Collection<IndexQueueModel>> taskMap) {
        RedisUtils.set(CacheKeys.INDEX_QUEUE_KEY,cacheKey,taskMap);
    }

    @Override
    public void clear() {
        RedisUtils.clear(CacheKeys.INDEX_QUEUE_KEY);
    }

    @Override
    public LinkedHashMap<EntityType, Collection<IndexQueueModel>> reset(){
        try {
            synchronized (logger) {
                LinkedHashMap<EntityType, Collection<IndexQueueModel>> newMap = new LinkedHashMap<>();
                newMap.putAll(get());
                clear();
                return newMap;
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }
}
