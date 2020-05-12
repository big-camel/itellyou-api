package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserBankDao;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserBankConfigModel;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankModel;
import com.itellyou.model.user.UserBankType;
import com.itellyou.service.user.UserBankConfigService;
import com.itellyou.service.user.UserBankLogService;
import com.itellyou.service.user.UserBankService;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = "bank_info")
@Service
public class UserBankServiceImpl implements UserBankService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserBankDao bankDao;

    private final UserBankLogService bankLogService;

    private final UserBankConfigService configService;

    @Autowired
    public UserBankServiceImpl(UserBankDao userBankDao, UserBankLogService bankLogService, UserBankConfigService configService){
        this.bankDao = userBankDao;
        this.bankLogService = bankLogService;
        this.configService = configService;
    }

    @Override
    @Cacheable
    public UserBankModel findByUserId(Long userId) {
        return bankDao.findByUserId(userId);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#userId")
    public UserBankLogModel update(Double amount, UserBankType type, EntityAction action, EntityType dataType, String dataKey, Long userId, String remark, Long clientIp) throws Exception {
        try{
            synchronized(userId) {
                int result = bankDao.update(amount, type, userId);
                if (result != 1) {
                    throw new Exception("更新失败");
                }
                UserBankModel userBankModel = bankDao.findByUserId(userId);
                if (userBankModel == null) {
                    throw new Exception("获取失败");
                }
                Double balance = 0.0;
                if (type == UserBankType.CREDIT) {
                    balance = userBankModel.getCredit().doubleValue();
                } else if (type == UserBankType.CASH) {
                    balance = userBankModel.getCash();
                } else if (type == UserBankType.SCORE) {
                    balance = userBankModel.getScore().doubleValue();
                }
                UserBankLogModel bankLogModel = new UserBankLogModel(amount, balance, type, action, dataType, dataKey, remark, DateUtils.getTimestamp(), clientIp, userId);
                result = bankLogService.insert(bankLogModel);
                if (result != 1) {
                    throw new Exception("写入日志失败");
                }
                return bankLogModel;
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    public int insert(UserBankModel bankModel) {
        return bankDao.insert(bankModel);
    }

    private boolean checkTotal(double totalLimit,long timeLimit,UserBankType bankType,EntityAction action,EntityType type,Long userId){
        Long now = DateUtils.getTimestamp();
        if(totalLimit > 0){
            double total = bankLogService.total(null,bankType,action,type,null,userId,
                    now - timeLimit ,now,null);
            if(total > totalLimit) {
                logger.warn("total of day {}, limit:{}", timeLimit / 86400, totalLimit);
                return true;
            }
        }
        return false;
    }

    private boolean checkCount(int countLimit,long timeLimit,UserBankType bankType,EntityAction action,EntityType type,Long userId){
        Long now = DateUtils.getTimestamp();
        if(countLimit > 0){
            int count = bankLogService.count(null,bankType,action,type,null,userId,
                    now - timeLimit ,now,null);
            if(count > countLimit) {
                logger.warn("count of day {}, limit:",timeLimit / 86400 , countLimit);
                return true;
            }
        }
        return false;
    }

    private boolean limitCheck(String checkType,UserBankConfigModel configModel,UserBankType bankType,OperationalModel model){
        double totalMonth = configModel.getTargeterTotalOfMonth();
        int countMonth = configModel.getTargeterCountOfMonth();
        double totalWeek = configModel.getTargeterTotalOfWeek();
        int countWeek = configModel.getTargeterCountOfWeek();
        double totalDay = configModel.getTargeterTotalOfDay();
        int countDay = configModel.getTargeterCountOfDay();
        Long userId = model.getTargetUserId();
        if(checkType.equals("creater")){
            totalMonth = configModel.getCreaterTotalOfMonth();
            countMonth = configModel.getCreaterCountOfMonth();
            totalWeek = configModel.getCreaterTotalOfWeek();
            countWeek = configModel.getCreaterCountOfWeek();
            totalDay = configModel.getCreaterTotalOfDay();
            countDay = configModel.getCreaterCountOfDay();
            userId = model.getCreatedUserId();
        }
        boolean limitCheck;
        limitCheck = checkTotal(totalMonth,86400 * 30,bankType,model.getAction(),model.getType(),userId);
        if(limitCheck) return true;
        limitCheck = checkCount(countMonth,86400 * 30,bankType,model.getAction(),model.getType(),userId);
        if(limitCheck) return true;
        limitCheck = checkTotal(totalWeek,86400 * 7,bankType,model.getAction(),model.getType(),userId);
        if(limitCheck) return true;
        limitCheck = checkCount(countWeek,86400 * 7,bankType,model.getAction(),model.getType(),userId);
        if(limitCheck) return true;
        limitCheck = checkTotal(totalDay,86400,bankType,model.getAction(),model.getType(),userId);
        if(limitCheck) return true;
        limitCheck = checkCount(countDay,86400,bankType,model.getAction(),model.getType(),userId);
        if(limitCheck) return true;
        if(configModel.isOnlyOnce()){
            int count = bankLogService.count(null,bankType,model.getAction(), model.getType(),model.getTargetId().toString(),userId,null,null,null);
            if(count > 0) {
                logger.warn("only once limit:{}",count);
                return true;
            }
        }
        return false;
    }


    @Override
    @Async
    public void updateByOperational(UserBankType bankType,OperationalModel model) {
        if(model == null) return;
        UserBankConfigModel configModel = configService.find(bankType,model.getAction(),model.getType());

        // 判断操作者是否满足需要最少权限分才能得分得情况
        if(configModel != null && configModel.getCreaterMinScore() > 0 && model.getCreatedUserId() != null){
            UserBankModel bankModel = findByUserId(model.getCreatedUserId());
            if(bankModel == null || bankModel.getScore() < configModel.getCreaterMinScore()) return;
        }
        // 设置操作对象用户的得分
        if(configModel != null && model.getTargetUserId() != null && configModel.getTargeterStep() != 0){
            try {
                synchronized(model.getTargetUserId()) {
                    //检测操作对象用户是否有限制得分情况
                    if(limitCheck("targeter",configModel,bankType,model)) return;
                    update(Double.valueOf(configModel.getTargeterStep()), bankType, model.getAction(), model.getType(), model.getTargetId().toString(),
                            model.getTargetUserId(), configModel.getTargeterRemark(), model.getCreatedIp());
                }
            }catch (Exception e){
                logger.error(e.getLocalizedMessage());
            }
        }
        // 设置操作者得分
        if(configModel != null && model.getCreatedUserId() != null && configModel.getCreaterStep() != 0){
            try {
                synchronized(model.getCreatedUserId()) {
                    //检测操作是否有限制得分情况
                    if(limitCheck("creater",configModel,bankType,model)) return;
                    update(Double.valueOf(configModel.getCreaterStep()), bankType, model.getAction(), model.getType(), model.getTargetId().toString(),
                            model.getCreatedUserId(), configModel.getCreaterRemark(), model.getCreatedIp());
                }
            }catch (Exception e){
                logger.error(e.getLocalizedMessage());
            }
        }
    }
}
