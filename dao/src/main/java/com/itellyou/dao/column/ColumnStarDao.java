package com.itellyou.dao.column;

import com.itellyou.model.column.ColumnStarModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ColumnStarDao {
    int insert(ColumnStarModel model);
    int delete(@Param("columnId") Long columnId, @Param("userId") Long userId);
    List<ColumnStarModel> search(@Param("columnIds") Collection<Long> columnIds,
                                 @Param("userId") Long userId,
                                 @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                 @Param("ip") Long ip,
                                 @Param("order") Map<String, String> order,
                                 @Param("offset") Integer offset,
                                 @Param("limit") Integer limit);
    int count(@Param("columnIds") Collection<Long> columnIds,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
