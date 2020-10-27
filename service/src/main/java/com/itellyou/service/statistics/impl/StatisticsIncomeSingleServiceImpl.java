package com.itellyou.service.statistics.impl;

import com.itellyou.dao.statistics.StatisticsIncomeDao;
import com.itellyou.model.statistics.StatisticsIncomeModel;
import com.itellyou.model.statistics.StatisticsIncomeTotalModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.service.statistics.StatisticsIncomeSingleService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsIncomeSingleServiceImpl implements StatisticsIncomeSingleService {

    private final StatisticsIncomeDao incomeDao;

    public StatisticsIncomeSingleServiceImpl(StatisticsIncomeDao incomeDao) {
        this.incomeDao = incomeDao;
    }

    @Override
    public List<StatisticsIncomeModel> search(Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return incomeDao.search(userId,beginDate,endDate,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime) {
        return incomeDao.count(userId,beginDate,endDate,beginTime,endTime);
    }

    @Override
    public List<StatisticsIncomeModel> searchGroupByDate(Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return incomeDao.searchGroupByDate(userId,beginDate,endDate,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int countGroupByDate(Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime) {
        return incomeDao.countGroupByDate(userId,beginDate,endDate,beginTime,endTime);
    }

    @Override
    public PageModel<StatisticsIncomeModel> pageGroupByDate(Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<StatisticsIncomeModel> list = searchGroupByDate(userId,beginDate,endDate,beginTime,endTime,order,offset,limit);
        Integer total = countGroupByDate(userId,beginDate,endDate,beginTime,endTime);
        return new PageModel<>(offset,limit,total,list);
    }

    @Override
    public PageModel<StatisticsIncomeModel> page(Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<StatisticsIncomeModel> list = search(userId,beginDate,endDate,beginTime,endTime,order,offset,limit);
        Integer total = count(userId,beginDate,endDate,beginTime,endTime);
        return new PageModel<>(offset,limit,total,list);
    }

    @Override
    public List<StatisticsIncomeTotalModel> totalByUser(Collection<Long> userIds, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return incomeDao.totalByUser(userIds,beginDate,endDate,beginTime,endTime,order,offset,limit);
    }
}
