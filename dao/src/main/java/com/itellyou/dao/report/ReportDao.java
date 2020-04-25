package com.itellyou.dao.report;

import com.itellyou.model.report.ReportAction;
import com.itellyou.model.report.ReportModel;
import com.itellyou.model.sys.EntityType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ReportDao {
    int insert(ReportModel model);

    List<ReportModel> search(@Param("id") Long id,
                                      @Param("actionsMap") Map<ReportAction, HashSet<EntityType>> actionsMap,
                                        @Param("state") Integer state,
                                      @Param("targetUserId") Long targetUserId,
                                      @Param("userId") Long userId,
                                      @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
                                      @Param("ip") Long ip,
                                      @Param("order") Map<String, String> order,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);
    int count(@Param("id") Long id,
              @Param("actionsMap") Map<ReportAction, HashSet<EntityType>> actionsMap,
              @Param("state") Integer state,
              @Param("targetUserId") Long targetUserId,
              @Param("userId") Long userId,
              @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,
              @Param("ip") Long ip);
}
