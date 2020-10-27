package com.itellyou.service.statistics.impl;

import com.itellyou.dao.statistics.StatisticsIncomeDao;
import com.itellyou.model.statistics.StatisticsIncomeModel;
import com.itellyou.service.statistics.StatisticsIncomeService;
import org.springframework.stereotype.Service;

@Service
public class StatisticsIncomeServiceImpl implements StatisticsIncomeService {

    private final StatisticsIncomeDao incomeDao;

    public StatisticsIncomeServiceImpl(StatisticsIncomeDao incomeDao) {
        this.incomeDao = incomeDao;
    }

    @Override
    public int insertOrUpdate(StatisticsIncomeModel... models) {
        return incomeDao.insertOrUpdate(models);
    }
}
