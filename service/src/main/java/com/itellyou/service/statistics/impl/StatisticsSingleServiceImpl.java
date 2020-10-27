package com.itellyou.service.statistics.impl;

import com.itellyou.dao.statistics.StatisticsInfoDao;
import com.itellyou.model.statistics.StatisticsInfoModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.service.statistics.StatisticsSingleService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsSingleServiceImpl implements StatisticsSingleService {

    private final StatisticsInfoDao infoDao;

    public StatisticsSingleServiceImpl(StatisticsInfoDao infoDao) {
        this.infoDao = infoDao;
    }

    @Override
    public List<StatisticsInfoModel> searchGroupByDate(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return infoDao.searchGroupByDate(userId,dataType,dataKeys,beginDate,endDate,beginTime,endTime,order,offset,limit);
    }

    @Override
    public List<StatisticsInfoModel> searchGroupByUserAndType(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return infoDao.searchGroupByUserAndType(userId,dataType,dataKeys,beginDate,endDate,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int countGroupByDate(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime) {
        return infoDao.countGroupByDate(userId,dataType,dataKeys,beginDate,endDate,beginTime,endTime);
    }

    @Override
    public PageModel<StatisticsInfoModel> pageGroupByDate(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        List<StatisticsInfoModel> list = searchGroupByDate(userId,dataType,dataKeys,beginDate,endDate,beginTime,endTime,order,offset,limit);
        Integer count = countGroupByDate(userId,dataType,dataKeys,beginDate,endDate,beginTime,endTime);
        return new PageModel<>(offset,limit,count,list);
    }

    @Override
    public List<StatisticsInfoModel> search(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit) {
        return infoDao.search(userId,dataType,dataKeys,beginDate,endDate,beginTime,endTime,order,offset,limit);
    }

    @Override
    public int count(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime) {
        return infoDao.count(userId,dataType,dataKeys,beginDate,endDate,beginTime,endTime);
    }
}
