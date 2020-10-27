package com.itellyou.service.statistics;

import com.itellyou.model.statistics.StatisticsIncomeModel;
import com.itellyou.model.statistics.StatisticsIncomeTotalModel;
import com.itellyou.model.sys.PageModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface StatisticsIncomeSingleService {

    List<StatisticsIncomeModel> search(Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order,
                                       Integer offset,
                                       Integer limit);

    int count(Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime);

    List<StatisticsIncomeModel> searchGroupByDate(Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order,
                                       Integer offset,
                                       Integer limit);

    int countGroupByDate(Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime);

    PageModel<StatisticsIncomeModel> pageGroupByDate(Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order,
                                          Integer offset,
                                          Integer limit);

    PageModel<StatisticsIncomeModel> page(Long userId, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order,
                                          Integer offset,
                                          Integer limit);

    List<StatisticsIncomeTotalModel> totalByUser(Collection<Long> userIds,
                                                 Long beginDate, Long endDate,
                                                 Long beginTime, Long endTime,
                                                 Map<String, String> order,
                                                 Integer offset,
                                                 Integer limit);
}
