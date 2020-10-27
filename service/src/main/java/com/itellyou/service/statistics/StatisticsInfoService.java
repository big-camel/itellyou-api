package com.itellyou.service.statistics;

import com.itellyou.model.statistics.StatisticsInfoModel;

public interface StatisticsInfoService {
    int insertOrUpdate(StatisticsInfoModel... models);

    int addViewCountById(Long id,Integer step,Long updatedTime, Long updatedIp);

    int addCommentCountById(Long id,Integer step,Long updatedTime, Long updatedIp);

    int addSupportCountById(Long id,Integer step,Long updatedTime, Long updatedIp);

    int addStarCountById(Long id,Integer step,Long updatedTime, Long updatedIp);
}
