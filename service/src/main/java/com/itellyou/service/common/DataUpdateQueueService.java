package com.itellyou.service.common;

import com.itellyou.model.common.DataUpdateQueueModel;
import com.itellyou.model.common.DataUpdateStepModel;
import com.itellyou.model.sys.EntityType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface DataUpdateQueueService {

    <T extends DataUpdateStepModel> void cumulative(T stepModel,T model);

    <T extends DataUpdateStepModel> void put(String cacheKey, DataUpdateQueueModel<T> model, BiConsumer<T,T> cumulative);

    <T extends DataUpdateStepModel> void put(String cacheKey, DataUpdateQueueModel<T> model);

    <T extends DataUpdateStepModel> void put(DataUpdateQueueModel<T> model);

    <T extends DataUpdateStepModel> void put(DataUpdateQueueModel<T> model, BiConsumer<T,T> cumulative);

    int size(String cacheKey);

    int size();

    <T extends DataUpdateStepModel> Map<Long,Map<Long, Map<EntityType, Collection<T>>>> get(String cacheKey);

    <T extends DataUpdateStepModel> Map<Long,Map<Long, Map<EntityType, Collection<T>>>> get();

    <T extends DataUpdateStepModel> void save(String cacheKey,Map<Long, Map<Long, Map<EntityType, Collection<T>>>> taskMap);

    <T extends DataUpdateStepModel> void save(Map<Long, Map<Long, Map<EntityType, Collection<T>>>> taskMap);

    void clear(String cacheKey);

    void clear();

    /**
     * 获取统计数据，并且清除Redis缓存，不在日期段的将保存到下一轮，在数据值为负的情况下此数据同样转到下一轮，并更新日期为当前日期
     * @param beginDate 指定开始日期的时间戳（不包含）
     * @param endDate 指定结束日期的时间戳（不包含）
     * @return
     */
    <T extends DataUpdateStepModel> Map<Long,Map<Long, Map<EntityType, Collection<T>>>> reset(String cacheKey,Long beginDate, Long endDate);

    <T extends DataUpdateStepModel> Map<Long,Map<Long, Map<EntityType, Collection<T>>>> reset(Long beginDate, Long endDate);

    /**
     * 获取全部统计数据，并且清除Redis缓存，在数据值为负的情况下此数据转到下一轮，并更新日期为当前日期
     * @return
     */
    <T extends DataUpdateStepModel> Map<Long,Map<Long, Map<EntityType, Collection<T>>>> reset(String cacheKey);

    <T extends DataUpdateStepModel> Map<Long,Map<Long, Map<EntityType, Collection<T>>>> reset();

    /**
     * 根据实体类型和实体编号获取统计值
     * @param type
     * @param id
     * @return
     */
    <T extends DataUpdateStepModel> T get(String cacheKey,EntityType type, Long id);

    <T extends DataUpdateStepModel> T get(EntityType type, Long id);

    /**
     * 批量根据实体类型和实体编号获取统计值
     * @param type
     * @param ids
     * @return
     */
    <T extends DataUpdateStepModel> List<T> get(String cacheKey,EntityType type, Collection<Long> ids,BiConsumer<T,T> cumulative);

    <T extends DataUpdateStepModel> List<T> get(String cacheKey,EntityType type, Collection<Long> ids);

    <T extends DataUpdateStepModel> List<T> get(EntityType type, Collection<Long> ids);

    <T extends DataUpdateStepModel> List<T> get(EntityType type, Collection<Long> ids,BiConsumer<T,T> cumulative);

    <T> void submit(List<T> data,Integer stepMax, Consumer<List<T>> callback);
}
