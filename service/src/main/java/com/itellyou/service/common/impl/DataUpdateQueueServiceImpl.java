package com.itellyou.service.common.impl;

import com.itellyou.model.common.DataUpdateQueueModel;
import com.itellyou.model.common.DataUpdateStepModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.common.DataUpdateQueueService;
import com.itellyou.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Service
public class DataUpdateQueueServiceImpl implements DataUpdateQueueService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String cacheName = CacheKeys.DATAUPDATE_QUEUE_KEY;
    private final static String defaultCacheKey = "default";

    @Override
    public <T extends DataUpdateStepModel> void cumulative(T stepModel,T model){
        stepModel.setViewStep(stepModel.getViewStep() + model.getViewStep());
        stepModel.setCommentStep(stepModel.getCommentStep() + model.getCommentStep());
        stepModel.setSupportStep(stepModel.getSupportStep() + model.getSupportStep());
        stepModel.setOpposeStep(stepModel.getOpposeStep() + model.getOpposeStep());
        stepModel.setStarStep(stepModel.getStarStep() + model.getStarStep());
    }

    @Override
    public <T extends DataUpdateStepModel> void put(String cacheKey, DataUpdateQueueModel<T> queueModel, BiConsumer<T, T> cumulative) {
        try {
            synchronized (logger) {
                Map<Long, Map<Long, Map<EntityType, Collection<T>>>> taskMap = get(cacheKey);
                // 获取队列集合
                Collection<T> queueModels = taskMap.computeIfAbsent(queueModel.getUserId(), key -> new HashMap<>()).computeIfAbsent(queueModel.getDate(), key -> new HashMap<>()).computeIfAbsent(queueModel.getDataType(), key -> new ArrayList<>());
                // 查找当天已统计的Model
                T model = queueModel.getData();
                Optional<T> stepModelOptional = queueModels.stream().filter(item -> item.getId().equals(model.getId())).findFirst();
                stepModelOptional.ifPresent(stepModel -> {
                    // 如果存在已统计的Model，则进行数据累加
                    cumulative.accept(stepModel,model);
                    // 保存到Redis
                    save(cacheKey,taskMap);
                });
                if (!stepModelOptional.isPresent()) {
                    // 未查找到已统计的Model，直接加入
                    queueModels.add(model);
                    // 保存到Redis
                    save(cacheKey,taskMap);
                }
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    /**
     * 加入队列
     * @param queueModel
     */
    @Override
    public <T extends DataUpdateStepModel> void put(String cacheKey,DataUpdateQueueModel<T> queueModel) {
        put(cacheKey,queueModel,this::cumulative);
    }

    @Override
    public <T extends DataUpdateStepModel> void put(DataUpdateQueueModel<T> queueModel, BiConsumer<T, T> cumulative) {
        put(defaultCacheKey,queueModel,cumulative);
    }

    @Override
    public void put(DataUpdateQueueModel model) {
        put(defaultCacheKey,model);
    }

    @Override
    public int size(String cacheKey) {
        Map<Long,Map<Long, Map<EntityType, Collection<DataUpdateStepModel>>>> taskMap = get(cacheKey);
        AtomicInteger size = new AtomicInteger();
        taskMap.values().forEach(value -> value.values().forEach(item -> size.addAndGet(item.size())));
        return size.get();
    }

    @Override
    public int size() {
        return size(defaultCacheKey);
    }

    @Override
    public <T extends DataUpdateStepModel> Map<Long,Map<Long, Map<EntityType, Collection<T>>>> get(String cacheKey) {
        List<DataUpdateQueueModel<T>> data = RedisUtils.get(cacheName,cacheKey,List.class);
        Map<Long,Map<Long, Map<EntityType, Collection<T>>>> taskMap = new HashMap<>();
        if(data == null) return taskMap;
        data.forEach(model -> {
            taskMap.computeIfAbsent(model.getUserId(),key -> new HashMap<>()).
                    computeIfAbsent(model.getDate(),key -> new HashMap<>()).
                    computeIfAbsent(model.getDataType(),key -> new ArrayList<>()).add(model.getData());
        });
        return taskMap;
    }

    @Override
    public <T extends DataUpdateStepModel> Map<Long, Map<Long, Map<EntityType, Collection<T>>>> get() {
        return get(defaultCacheKey);
    }

    @Override
    public <T extends DataUpdateStepModel> void save(String cacheKey,Map<Long,Map<Long, Map<EntityType, Collection<T>>>> taskMap) {
        List<DataUpdateQueueModel> data = new ArrayList<>();
        taskMap.forEach((userId,childes) -> {
            childes.forEach((dateKey,values) -> {
                values.forEach((type,models) -> {
                    models.forEach(stepModel -> {
                        data.add(new DataUpdateQueueModel(userId,type,dateKey,stepModel));
                    });
                });
            });
        });
        RedisUtils.set(cacheName, cacheKey, data);
    }

    @Override
    public <T extends DataUpdateStepModel> void save(Map<Long, Map<Long, Map<EntityType, Collection<T>>>> taskMap) {
        save(defaultCacheKey,taskMap);
    }

    @Override
    public void clear(String cacheKey) {
        RedisUtils.remove(cacheName,cacheKey);
    }

    @Override
    public void clear() {
        clear(defaultCacheKey);
    }

    @Override
    public <T extends DataUpdateStepModel> Map<Long,Map<Long, Map<EntityType, Collection<T>>>> reset(String cacheKey,Long beginDate,Long endDate){

        try {
            synchronized (logger) {
                Map<Long, Map<Long, Map<EntityType, Collection<T>>>> data = get(cacheKey);
                // 清除redis
                clear(cacheKey);
                // 过滤指定日期数据
                Map<Long, Map<Long, Map<EntityType, Collection<T>>>> newMap = new HashMap<>();
                data.forEach((userId, childes) -> {
                    childes.forEach((dateKey, values) -> {
                        boolean isSubmit = (beginDate == null || dateKey > beginDate) && (endDate == null || dateKey < endDate);
                        values.forEach((type, models) -> {
                            models.forEach(stepModel -> {
                                if (isSubmit) {
                                    //加入到需要统计的数据队列
                                    newMap.computeIfAbsent(userId, key -> new HashMap<>()).computeIfAbsent(dateKey, key -> new HashMap<>()).computeIfAbsent(type, key -> new ArrayList<>()).add(stepModel);
                                } else {
                                    //保存到下一轮
                                    put(cacheKey,new DataUpdateQueueModel(userId, type, dateKey, stepModel));
                                }
                            });
                        });
                    });
                });
                return newMap;
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public <T extends DataUpdateStepModel> Map<Long, Map<Long, Map<EntityType, Collection<T>>>> reset(Long beginDate, Long endDate) {
        return reset(defaultCacheKey,beginDate,endDate);
    }

    @Override
    public <T extends DataUpdateStepModel> Map<Long, Map<Long, Map<EntityType, Collection<T>>>> reset(String cacheKey) {
        return reset(cacheKey,null,null);
    }

    @Override
    public <T extends DataUpdateStepModel> Map<Long, Map<Long, Map<EntityType, Collection<T>>>> reset() {
        return reset(defaultCacheKey);
    }

    @Override
    public <T extends DataUpdateStepModel> T get(String cacheKey,EntityType type, Long id) {
        List<T> stepModels =  get(cacheKey,type,new HashSet<Long>(){{ add(id);}});
        return stepModels.size() > 0 ? stepModels.get(0) : null;
    }

    @Override
    public <T extends DataUpdateStepModel> T get(EntityType type, Long id) {
        return get(defaultCacheKey,type,id);
    }

    @Override
    public <T extends DataUpdateStepModel> List<T> get(String cacheKey, EntityType type, Collection<Long> ids, BiConsumer<T, T> cumulative) {
        Map<Long,Map<Long, Map<EntityType, Collection<T>>>> data = get(cacheKey);
        List<T> stepModels = new ArrayList<>();
        data.forEach((userId,values) -> {
            values.forEach((date,entityValues) -> {
                Collection<T> models = entityValues.computeIfAbsent(type,key -> new ArrayList<>());
                models.forEach(stepModel -> {
                    if(ids.contains(stepModel.getId())){
                        Optional<T> stepModelOptional = stepModels.stream().filter(model -> model.getId().equals(stepModel.getId())).findFirst();
                        // 如果已经在列表了，就累加数据
                        stepModelOptional.ifPresent(model -> {
                            cumulative.accept(stepModel,model);
                        });
                        // 不在列表，则加入
                        if(!stepModelOptional.isPresent()) {
                            stepModels.add(stepModel);
                        }
                    }
                });
            });

        });
        return stepModels;
    }

    @Override
    public <T extends DataUpdateStepModel> List<T> get(String cacheKey,EntityType type, Collection<Long> ids) {
        return get(cacheKey,type,ids,this::cumulative);
    }

    @Override
    public <T extends DataUpdateStepModel> List<T> get(EntityType type, Collection<Long> ids) {
        return get(defaultCacheKey,type,ids);
    }

    @Override
    public <T extends DataUpdateStepModel> List<T> get(EntityType type, Collection<Long> ids, BiConsumer<T, T> cumulative) {
        return get(defaultCacheKey,type,ids,cumulative);
    }

    @Override
    public  <T> void submit(List<T> data,Integer stepMax, Consumer<List<T>> callback){
        if(data == null) return;
        List<T> temp = new ArrayList<>();
        for (int step = 0; step < data.size();step ++){
            temp.add(data.get(step));
            if(step % stepMax == 0 || step == data.size() - 1){
                callback.accept(temp);
                temp.clear();
            }
        }
    }
}
