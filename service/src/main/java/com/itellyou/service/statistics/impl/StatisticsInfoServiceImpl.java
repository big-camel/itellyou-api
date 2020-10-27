package com.itellyou.service.statistics.impl;

import com.itellyou.dao.statistics.StatisticsInfoDao;
import com.itellyou.model.statistics.StatisticsInfoModel;
import com.itellyou.service.statistics.StatisticsInfoService;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class StatisticsInfoServiceImpl implements StatisticsInfoService {

    private final StatisticsInfoDao infoDao;

    public StatisticsInfoServiceImpl(StatisticsInfoDao infoDao) {
        this.infoDao = infoDao;
    }

    @Override
    public int insertOrUpdate(StatisticsInfoModel... models) {
        return this.infoDao.insertOrUpdate(models);
    }

    @Override
    public int addViewCountById(Long id, Integer step, Long updatedTime, Long updatedIp) {
        return this.infoDao.addCountById(id,new HashMap<String, Integer>(){{ put("view_count",step); }},updatedTime,updatedIp);
    }

    @Override
    public int addCommentCountById(Long id, Integer step, Long updatedTime, Long updatedIp) {
        return this.infoDao.addCountById(id,new HashMap<String, Integer>(){{ put("comment_count",step); }},updatedTime,updatedIp);
    }

    @Override
    public int addSupportCountById(Long id, Integer step, Long updatedTime, Long updatedIp) {
        return this.infoDao.addCountById(id,new HashMap<String, Integer>(){{ put("support_count",step); }},updatedTime,updatedIp);
    }

    @Override
    public int addStarCountById(Long id, Integer step, Long updatedTime, Long updatedIp) {
        return this.infoDao.addCountById(id,new HashMap<String, Integer>(){{ put("star_count",step); }},updatedTime,updatedIp);
    }
}
