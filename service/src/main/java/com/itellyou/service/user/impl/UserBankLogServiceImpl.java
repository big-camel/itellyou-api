package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserBankLogDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankLogType;
import com.itellyou.model.user.UserBankType;
import com.itellyou.service.user.UserBankLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    public List<UserBankLogModel> search(String id, UserBankType type, UserBankLogType dataType, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return userBankLogDao.search(id,type,dataType,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(String id, UserBankType type, UserBankLogType dataType, Long userId, Long beginTime, Long endTime, Long ip) {
        return userBankLogDao.count(id,type,dataType,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<UserBankLogModel> page(String id, UserBankType type, UserBankLogType dataType, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 20;
        List<UserBankLogModel> data = search(id,type,dataType,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(id,type,dataType,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }
}
