package com.itellyou.dao.statistics;

import com.itellyou.model.statistics.StatisticsInfoModel;
import com.itellyou.model.sys.EntityType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface StatisticsInfoDao {

    int insertOrUpdate(@Param("models") StatisticsInfoModel... models);

    List<StatisticsInfoModel> search(@Param("userId") Long userId, @Param("dataType") EntityType dataType, @Param("dataKeys") Collection<Long> dataKeys, @Param("beginDate") Long beginDate, @Param("endDate") Long endDate, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                               @Param("order") Map<String,String> order,
                               @Param("offset") Integer offset,
                               @Param("limit") Integer limit);

    int count(@Param("userId") Long userId,@Param("dataType") EntityType dataType, @Param("dataKeys") Collection<Long> dataKeys, @Param("beginDate") Long beginDate, @Param("endDate") Long endDate, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

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
    List<StatisticsInfoModel> searchGroupByDate(@Param("userId") Long userId, @Param("dataType") EntityType dataType, @Param("dataKeys") Collection<Long> dataKeys, @Param("beginDate") Long beginDate, @Param("endDate") Long endDate, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,@Param("order") Map<String,String> order,
                                                @Param("offset") Integer offset,
                                                @Param("limit") Integer limit);

    List<StatisticsInfoModel> searchGroupByUserAndType(@Param("userId") Long userId, @Param("dataType") EntityType dataType, @Param("dataKeys") Collection<Long> dataKeys, @Param("beginDate") Long beginDate, @Param("endDate") Long endDate, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,@Param("order") Map<String,String> order,
                                                       @Param("offset") Integer offset,
                                                       @Param("limit") Integer limit);

    int countGroupByDate(@Param("userId") Long userId, @Param("dataType") EntityType dataType, @Param("dataKeys") Collection<Long> dataKeys, @Param("beginDate") Long beginDate, @Param("endDate") Long endDate, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    int addCountById(@Param("id") Long id,@Param("fieldValues") Map<String,Integer> fieldValues,@Param("updatedTime") Long updatedTime, @Param("updatedIp") Long updatedIp);
}
