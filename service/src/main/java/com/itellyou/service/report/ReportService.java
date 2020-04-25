package com.itellyou.service.report;

import com.itellyou.model.report.ReportAction;
import com.itellyou.model.report.ReportModel;
import com.itellyou.model.sys.EntityType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface ReportService {
    int insert(ReportAction action, EntityType type, Long targetId, String description, Long userId, Long ip) throws Exception;

    List<ReportModel> search(Long id,
                                      Map<ReportAction, HashSet<EntityType>> actionsMap,
                                      Integer state,
                                      Long targetUserId,
                                      Long userId,
                                      Long beginTime, Long endTime,
                                      Long ip,
                                      Map<String, String> order,
                                      Integer offset,
                                      Integer limit);
    int count(Long id,
              Map<ReportAction, HashSet<EntityType>> actionsMap,
              Integer state,
              Long targetUserId,
              Long userId,
              Long beginTime, Long endTime,
              Long ip);
}
