package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysIncomeRelatedDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.SysIncomeRelatedModel;
import com.itellyou.service.sys.SysIncomeRelatedSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class SysIncomeRelatedSingleServiceImpl implements SysIncomeRelatedSingleService {

    private final SysIncomeRelatedDao relatedDao;

    public SysIncomeRelatedSingleServiceImpl(SysIncomeRelatedDao relatedDao) {
        this.relatedDao = relatedDao;
    }

    @Override
    public List<SysIncomeRelatedModel> search(Collection<Long> ids,Long incomeId,Long configId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetch(CacheKeys.SYS_INCOME_RELATED, SysIncomeRelatedModel.class,ids,(Collection<Long> fetchIds) ->
                relatedDao.search(fetchIds,incomeId,configId,userId,beginTime,endTime,ip,order,offset,limit));
    }

    @Override
    public int count(Collection<Long> ids,Long incomeId,Long configId, Long userId, Long beginTime, Long endTime, Long ip) {
        return relatedDao.count(ids,incomeId,configId,userId,beginTime,endTime,ip);
    }
}
