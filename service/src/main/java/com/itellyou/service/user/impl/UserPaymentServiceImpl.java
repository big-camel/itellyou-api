package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserPaymentDao;
import com.itellyou.model.user.*;
import com.itellyou.service.user.UserBankService;
import com.itellyou.service.user.UserPaymentServer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

@Service
public class UserPaymentServiceImpl implements UserPaymentServer {

    private final UserPaymentDao paymentDao;
    private final UserBankService bankService;

    public UserPaymentServiceImpl(UserPaymentDao paymentDao, UserBankService bankService) {
        this.paymentDao = paymentDao;
        this.bankService = bankService;
    }

    @Override
    public int insert(UserPaymentModel model) {
        return paymentDao.insert(model);
    }

    @Override
    public List<UserPaymentDetailModel> search(String id, UserPaymentStatus status, UserPaymentType type, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return paymentDao.search(id,status,type,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(String id, UserPaymentStatus status, UserPaymentType type, Long userId, Long beginTime, Long endTime, Long ip) {
        return paymentDao.count(id,status,type,userId,beginTime,endTime,ip);
    }

    @Override
    public UserPaymentDetailModel getDetail(String id) {
        List<UserPaymentDetailModel> list = search(id,null,null,null,null,null,null,null,null,null);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    @Override
    @Transactional
    public int updateStatus(String id, UserPaymentStatus status, Long updatedUserId, Long updatedTime, Long updatedIp) throws Exception {
        try {
            int result = paymentDao.updateStatus(id, status, updatedUserId, updatedTime, updatedIp);
            if(result == 1 && status.equals(UserPaymentStatus.SUCCEED)){
                UserPaymentModel paymentModel = getDetail(id);
                String type =  "";
                if(paymentModel.getType().equals(UserPaymentType.ALIPAY)) type = "支付宝";
                if(paymentModel.getType().equals(UserPaymentType.WECHAT)) type = "微信";
                result = bankService.update(paymentModel.getAmount(), UserBankType.CASH,updatedUserId,type + "充值成功",UserBankLogType.PAY,id,updatedIp);
                if(result != 1) throw new Exception("更新余额失败");
            }
            return result;
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }
}
