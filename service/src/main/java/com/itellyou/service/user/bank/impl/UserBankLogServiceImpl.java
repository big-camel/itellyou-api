package com.itellyou.service.user.bank.impl;

import com.itellyou.dao.user.UserBankLogDao;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankType;
import com.itellyou.service.user.bank.UserBankLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserBankLogServiceImpl implements UserBankLogService {

    private final UserBankLogDao userBankLogDao;

    @Autowired
    public UserBankLogServiceImpl(UserBankLogDao userBankLogDao){
        this.userBankLogDao = userBankLogDao;
    }

    @Override
    public int insert(UserBankLogModel userBankLogModel) {
        return userBankLogDao.insert(userBankLogModel);
    }

    @Override
    public double total(Set<Long> ids, UserBankType type, EntityAction action, EntityType dataType, String dataKey, Long userId, Long beginTime, Long endTime, Long ip) {
        return userBankLogDao.total(ids,type,action,dataType,dataKey,userId,beginTime,endTime,ip);
    }
}
