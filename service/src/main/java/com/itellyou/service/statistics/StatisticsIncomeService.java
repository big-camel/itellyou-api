package com.itellyou.service.statistics;

import com.itellyou.model.statistics.StatisticsIncomeModel;

public interface StatisticsIncomeService {
    int insertOrUpdate(StatisticsIncomeModel... models);
}
