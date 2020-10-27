package com.itellyou.service.user.bank.impl;

import com.itellyou.dao.user.UserBankLogDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankType;
import com.itellyou.service.user.bank.UserBankLogSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = CacheKeys.BANK_LOG_KEY)
@Service
public class UserBankLogSingleServiceImpl implements UserBankLogSingleService {

    private final UserBankLogDao logDao;

    public UserBankLogSingleServiceImpl(UserBankLogDao logDao) {
        this.logDao = logDao;
    }

    @Override
    public List<UserBankLogModel> search(Collection<Long> ids, UserBankType type, EntityAction action, EntityType dataType, String dataKey, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetch(CacheKeys.BANK_LOG_KEY, UserBankLogModel.class,ids,(Collection<Long> fetchIds) ->
                logDao.search(fetchIds,type,action,dataType,dataKey,userId,beginTime,endTime,ip,order,offset,limit)
        );
    }
}
