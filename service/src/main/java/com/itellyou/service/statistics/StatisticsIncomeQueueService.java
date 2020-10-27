package com.itellyou.service.statistics;

import com.itellyou.model.statistics.StatisticsIncomeQueueModel;
import com.itellyou.model.statistics.StatisticsIncomeStepModel;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface StatisticsIncomeQueueService {

    <T extends StatisticsIncomeStepModel> void cumulative(T stepModel, T model);

    <T extends StatisticsIncomeStepModel> void put(String cacheKey, StatisticsIncomeQueueModel<T> model, BiConsumer<T,T> cumulative);

    <T extends StatisticsIncomeStepModel> void put(String cacheKey, StatisticsIncomeQueueModel<T> model);

    <T extends StatisticsIncomeStepModel> void put(StatisticsIncomeQueueModel<T> model);

    <T extends StatisticsIncomeStepModel> void put(StatisticsIncomeQueueModel<T> model, BiConsumer<T,T> cumulative);

    int size(String cacheKey);

    int size();

    <T extends StatisticsIncomeStepModel> Map<Long,Map<Long, T>> get(String cacheKey);

    <T extends StatisticsIncomeStepModel> Map<Long,Map<Long, T>> get();

    <T extends StatisticsIncomeStepModel> void save(String cacheKey,Map<Long,Map<Long, T>> taskMap);

    <T extends StatisticsIncomeStepModel> void save(Map<Long,Map<Long, T>> taskMap);

    void clear(String cacheKey);

    void clear();

    /**
     * 获取统计数据，并且清除Redis缓存，不在日期段的将保存到下一轮，在数据值为负的情况下此数据同样转到下一轮，并更新日期为当前日期
     * @param beginDate 指定开始日期的时间戳（不包含）
     * @param endDate 指定结束日期的时间戳（不包含）
     * @return
     */
    <T extends StatisticsIncomeStepModel> Map<Long,Map<Long, T>> reset(String cacheKey, Long beginDate, Long endDate);

    <T extends StatisticsIncomeStepModel> Map<Long,Map<Long, T>> reset(Long beginDate, Long endDate);

    /**
     * 获取全部统计数据，并且清除Redis缓存，在数据值为负的情况下此数据转到下一轮，并更新日期为当前日期
     * @return
     */
    <T extends StatisticsIncomeStepModel> Map<Long,Map<Long, T>> reset(String cacheKey);

    <T extends StatisticsIncomeStepModel> Map<Long,Map<Long, T>> reset();

    <T> void submit(List<T> data, Integer stepMax, Consumer<List<T>> callback);
}
