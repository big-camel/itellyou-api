package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserBankDao;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankLogType;
import com.itellyou.model.user.UserBankModel;
import com.itellyou.model.user.UserBankType;
import com.itellyou.service.user.UserBankLogService;
import com.itellyou.service.user.UserBankService;
import com.itellyou.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class UserBankServiceImpl implements UserBankService {

    private final UserBankDao bankDao;

    private final UserBankLogService bankLogService;

    @Autowired
    public UserBankServiceImpl(UserBankDao userBankDao,UserBankLogService bankLogService){
        this.bankDao = userBankDao;
        this.bankLogService = bankLogService;
    }

    @Override
    public UserBankModel findByUserId(Long userId) {
        return bankDao.findByUserId(userId);
    }

    @Override
    @Transactional
    public int update(Double amount, UserBankType type, Long userId, String remark, UserBankLogType dataType, String dataKey, Long clientIp) {
        try{
            int result = bankDao.update(amount,type,userId);
            if(result != 1){
                throw new Exception("更新余额失败");
            }
            UserBankModel userBankModel = bankDao.findByUserId(userId);
            if(userBankModel == null){
                throw new Exception("获取余额失败");
            }
            Double balance = 0.0;
            if(type == UserBankType.CREDIT){
                balance = userBankModel.getCredit().doubleValue();
            }else if(type == UserBankType.CASH){
                balance = userBankModel.getCash();
            }
            UserBankLogModel bankLogModel = new UserBankLogModel( amount, type, balance, dataType, dataKey, remark, DateUtils.getTimestamp(), clientIp, userId );
            result = bankLogService.insert(bankLogModel);
            if(result != 1){
                throw new Exception("写入日志失败");
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    public int update(Double amount, UserBankType type, Long userId, String remark, Long clientIp) {
        return update(amount,type,userId,remark,UserBankLogType.DEFAULT,"",clientIp);
    }

    @Override
    public int insert(UserBankModel bankModel) {
        return bankDao.insert(bankModel);
    }
}
