package com.itellyou.dao.statistics;

import com.itellyou.model.statistics.StatisticsIncomeModel;
import com.itellyou.model.statistics.StatisticsIncomeTotalModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface StatisticsIncomeDao {

    int insertOrUpdate(@Param("models") StatisticsIncomeModel... models);

    List<StatisticsIncomeModel> search(@Param("userId") Long userId, @Param("beginDate") Long beginDate, @Param("endDate") Long endDate, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                       @Param("order") Map<String, String> order,
                                       @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);

    int count(@Param("userId") Long userId, @Param("beginDate") Long beginDate, @Param("endDate") Long endDate, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    List<StatisticsIncomeModel> searchGroupByDate(@Param("userId") Long userId, @Param("beginDate") Long beginDate, @Param("endDate") Long endDate, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                       @Param("order") Map<String, String> order,
                                       @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);

    int countGroupByDate(@Param("userId") Long userId, @Param("beginDate") Long beginDate, @Param("endDate") Long endDate, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);


    int addAmountById(@Param("id") Long id, @Param("fieldValues") Map<String, Integer> fieldValues, @Param("updatedTime") Long updatedTime, @Param("updatedIp") Long updatedIp);

    List<StatisticsIncomeTotalModel> totalByUser(@Param("userIds") Collection<Long> userIds,
                                                 @Param("beginDate") Long beginDate, @Param("endDate") Long endDate,
                                                 @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                                 @Param("order") Map<String, String> order,
                                                 @Param("offset") Integer offset,
                                                 @Param("limit") Integer limit);
}
