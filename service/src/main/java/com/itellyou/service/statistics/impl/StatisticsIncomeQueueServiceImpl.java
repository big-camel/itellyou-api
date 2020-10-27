package com.itellyou.service.statistics.impl;

import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.statistics.StatisticsIncomeQueueModel;
import com.itellyou.model.statistics.StatisticsIncomeStepModel;
import com.itellyou.service.statistics.StatisticsIncomeQueueService;
import com.itellyou.util.ArithmeticUtils;
import com.itellyou.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Service
public class StatisticsIncomeQueueServiceImpl implements StatisticsIncomeQueueService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String cacheName = CacheKeys.STATISTICS_INCOME_QUEUE_KEY;
    private final static String defaultCacheKey = "default";

    @Override
    public <T extends StatisticsIncomeStepModel> void cumulative(T stepModel, T model) {
        stepModel.setTipStep(ArithmeticUtils.add(stepModel.getTipStep(),model.getTipStep()));
        stepModel.setRewardStep(ArithmeticUtils.add(stepModel.getRewardStep(),model.getRewardStep()));
        stepModel.setSharingStep(ArithmeticUtils.add(stepModel.getSharingStep(), model.getSharingStep()));
        stepModel.setSellStep(ArithmeticUtils.add(stepModel.getSellStep(),model.getSellStep()));
        stepModel.setOtherStep(ArithmeticUtils.add(stepModel.getOtherStep(),model.getOtherStep()));
        stepModel.setTotalStep(ArithmeticUtils.add(stepModel.getTipStep() ,stepModel.getRewardStep() ,stepModel.getSharingStep() ,stepModel.getSellStep() ,stepModel.getOtherStep()));
    }

    @Override
    public <T extends StatisticsIncomeStepModel> void put(String cacheKey, StatisticsIncomeQueueModel<T> model, BiConsumer<T, T> cumulative) {
        try {
            synchronized (logger) {
                Map<Long,Map<Long, T>> taskMap = get(cacheKey);
                // 获取队列集合
                Map<Long, T> dateMap = taskMap.computeIfAbsent(model.getUserId(), key -> new HashMap<>());
                T stepModel = model.getData();
                if(dateMap.containsKey(model.getDate())){
                    // 如果存在已统计的Model，则进行数据累加
                    cumulative.accept(dateMap.get(model.getDate()),stepModel);
                    // 保存到Redis
                    save(cacheKey,taskMap);
                }else{
                    // 未查找到已统计的Model，直接加入
                    dateMap.put(model.getDate(),stepModel);
                    // 保存到Redis
                    save(cacheKey,taskMap);
                }
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public <T extends StatisticsIncomeStepModel> void put(String cacheKey, StatisticsIncomeQueueModel<T> model) {
        put(cacheKey,model,this::cumulative);
    }

    @Override
    public <T extends StatisticsIncomeStepModel> void put(StatisticsIncomeQueueModel<T> model) {
        put(defaultCacheKey,model);
    }

    @Override
    public <T extends StatisticsIncomeStepModel> void put(StatisticsIncomeQueueModel<T> model, BiConsumer<T, T> cumulative) {
        put(defaultCacheKey,model,cumulative);
    }

    @Override
    public int size(String cacheKey) {
        Map<Long,Map<Long,StatisticsIncomeStepModel>> taskMap = get(cacheKey);
        AtomicInteger size = new AtomicInteger();
        taskMap.values().forEach(value -> size.addAndGet(value.values().size()));
        return size.get();
    }

    @Override
    public int size() {
        return size(defaultCacheKey);
    }

    @Override
    public <T extends StatisticsIncomeStepModel> Map<Long,Map<Long, T>> get(String cacheKey) {
        List<StatisticsIncomeQueueModel<T>> data = RedisUtils.get(cacheName,cacheKey,List.class);
        Map<Long,Map<Long, T>> taskMap = new HashMap<>();
        if(data == null) return taskMap;
        for (StatisticsIncomeQueueModel<T> model : data){
            taskMap.computeIfAbsent(model.getUserId(),key -> new HashMap<>()).put(model.getDate(),model.getData());
        }
        return taskMap;
    }

    @Override
    public <T extends StatisticsIncomeStepModel> Map<Long,Map<Long, T>> get() {
        return get(defaultCacheKey);
    }

    @Override
    public <T extends StatisticsIncomeStepModel> void save(String cacheKey, Map<Long,Map<Long, T>> taskMap) {
        List<StatisticsIncomeQueueModel<T>> data = new ArrayList<>();
        taskMap.values().forEach(values -> {
            values.forEach((date,model) -> {
                data.add(new StatisticsIncomeQueueModel<>(model.getUserId(),date,model));
            });
        });
        RedisUtils.set(cacheName, cacheKey, data);
    }

    @Override
    public <T extends StatisticsIncomeStepModel> void save(Map<Long,Map<Long, T>> taskMap) {
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
    public <T extends StatisticsIncomeStepModel> Map<Long,Map<Long, T>> reset(String cacheKey, Long beginDate, Long endDate) {
        try {
            synchronized (logger) {
                Map<Long,Map<Long, T>> data = get(cacheKey);
                // 清除redis
                clear(cacheKey);
                // 过滤指定日期数据
                Map<Long,Map<Long, T>> newMap = new HashMap<>();
                data.forEach((userId, dateModels) -> {
                    dateModels.forEach((date,model) -> {
                        boolean isSubmit = (beginDate == null || date > beginDate) && (endDate == null || date < endDate);
                        if (isSubmit) {
                            //加入到需要统计的数据队列
                            newMap.computeIfAbsent(userId,key -> new HashMap<>()).put(date,model);
                        } else {
                            //保存到下一轮
                            put(cacheKey,new StatisticsIncomeQueueModel<>(userId,date,model));
                        }
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
    public <T extends StatisticsIncomeStepModel> Map<Long,Map<Long, T>> reset(Long beginDate, Long endDate) {
        return reset(defaultCacheKey,beginDate,endDate);
    }

    @Override
    public <T extends StatisticsIncomeStepModel> Map<Long,Map<Long, T>> reset(String cacheKey) {
        return reset(cacheKey,null,null);
    }

    @Override
    public <T extends StatisticsIncomeStepModel> Map<Long,Map<Long, T>> reset() {
        return reset(defaultCacheKey);
    }

    @Override
    public <T> void submit(List<T> data, Integer stepMax, Consumer<List<T>> callback) {
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
