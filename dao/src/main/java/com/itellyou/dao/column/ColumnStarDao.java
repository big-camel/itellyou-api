package com.itellyou.dao.column;

import com.itellyou.model.column.ColumnStarDetailModel;
import com.itellyou.model.column.ColumnStarModel;
import com.itellyou.model.user.UserStarDetailModel;
import com.itellyou.model.user.UserStarModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ColumnStarDao {
    int insert(ColumnStarModel model);
    int delete(@Param("columnId") Long columnId, @Param("userId") Long userId);
    List<ColumnStarDetailModel> search(@Param("columnId") Long columnId,
                                       @Param("userId") Long userId,
                                       @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                       @Param("ip") Long ip,
                                       @Param("order") Map<String, String> order,
                                       @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);
    int count(@Param("columnId") Long columnId,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
