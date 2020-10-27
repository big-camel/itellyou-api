package com.itellyou.service.statistics;

import com.itellyou.model.statistics.StatisticsDetailModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface StatisticsSearchService {

    List<StatisticsDetailModel> search(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit);

    PageModel<StatisticsDetailModel> page(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String, String> order, Integer offset, Integer limit);

}
