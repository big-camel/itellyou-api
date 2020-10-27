package com.itellyou.service.statistics;

import com.itellyou.model.statistics.StatisticsInfoModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface StatisticsSingleService {

    /**
     * 根据日期分组条件查询
     * @param userId 用户编号
     * @param dataType 实体类型
     * @param dataKeys 实体编号
     * @param beginDate 开始日期（统计所属日期）
     * @param endDate 结束日期（统计所属日期）
     * @param beginTime 开始时间（创建时间）
     * @param endTime 结束时间（创建时间）
     * @param order 排序
     * @param offset 开始条数
     * @param limit 结束条数
     * @return
     */
    List<StatisticsInfoModel> searchGroupByDate(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String,String> order,
                                                Integer offset,
                                                Integer limit);

    List<StatisticsInfoModel> searchGroupByUserAndType(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String,String> order,
                                                       Integer offset,
                                                       Integer limit);

    int countGroupByDate(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime);

    PageModel<StatisticsInfoModel> pageGroupByDate(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String,String> order,
                                                   Integer offset,
                                                   Integer limit);

    List<StatisticsInfoModel> search(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime, Map<String,String> order,
                                       Integer offset,
                                       Integer limit);

    int count(Long userId, EntityType dataType, Collection<Long> dataKeys, Long beginDate, Long endDate, Long beginTime, Long endTime);
}
