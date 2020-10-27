package com.itellyou.service.statistics;

import com.itellyou.model.statistics.StatisticsIncomeQueueModel;
import com.itellyou.model.statistics.StatisticsIncomeStepModel;

import java.util.function.BiConsumer;

public interface StatisticsIncomeManageService {

    <T extends StatisticsIncomeStepModel> void put(StatisticsIncomeQueueModel<T> model);

    <T extends StatisticsIncomeStepModel> void put(StatisticsIncomeQueueModel<T> model, BiConsumer<T,T> cumulative);

    <T extends StatisticsIncomeStepModel> void cumulative(T stepModel,T model);

    void run();
}
