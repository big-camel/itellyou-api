package com.itellyou.service.user.bank.impl;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.itellyou.dao.user.UserPaymentDao;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.event.OperationalEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.*;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.thirdparty.AlipayService;
import com.itellyou.service.sys.uid.UidGenerator;
import com.itellyou.service.user.bank.UserBankService;
import com.itellyou.service.user.bank.UserPaymentService;
import com.itellyou.service.user.bank.UserPaymentSingleService;
import com.itellyou.util.DateUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = CacheKeys.PAYMENT_KEY)
@Service
public class UserPaymentServiceImpl implements UserPaymentService {

    private final UserPaymentDao paymentDao;
    private final UserBankService bankService;
    private final AlipayService alipayService;
    private final UidGenerator cachedUidGenerator;
    private final OperationalPublisher operationalPublisher;
    private final UserPaymentSingleService singleService;

    public UserPaymentServiceImpl(UserPaymentDao paymentDao, UserBankService bankService, AlipayService alipayService, UidGenerator cachedUidGenerator, OperationalPublisher operationalPublisher, UserPaymentSingleService singleService) {
        this.paymentDao = paymentDao;
        this.bankService = bankService;
        this.alipayService = alipayService;
        this.cachedUidGenerator = cachedUidGenerator;
        this.operationalPublisher = operationalPublisher;
        this.singleService = singleService;
    }

    @Override
    public int insert(UserPaymentModel model) {
        return paymentDao.insert(model);
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional
    public int updateStatus(String id, UserPaymentStatus status, Long updatedUserId, Long updatedTime, Long updatedIp) throws Exception {
        try {
            int result = paymentDao.updateStatus(id, status, updatedUserId, updatedTime, updatedIp);
            if(result == 1 && status.equals(UserPaymentStatus.SUCCEED)){
                UserPaymentModel paymentModel = singleService.find(id);
                UserBankLogModel logModel = bankService.update(paymentModel.getAmount(), UserBankType.CASH, EntityAction.PAYMENT,EntityType.PAYMENT,id,updatedUserId,paymentModel.getType().getName() + "充值成功",updatedIp);
                if(logModel == null) throw new Exception("更新余额失败");

                OperationalModel operationalModel = new OperationalModel(EntityAction.PAYMENT, EntityType.PAYMENT,logModel.getId(),updatedUserId,updatedUserId,DateUtils.toLocalDateTime(), updatedIp);
                operationalPublisher.publish(new OperationalEvent(this,operationalModel));
            }
            return result;
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    public AlipayTradePrecreateResponse preCreateAlipay(String subject, double amount, Long userId, Long ip) throws Exception {
        String id = "AP" + cachedUidGenerator.getUID();
        AlipayTradePrecreateResponse response = alipayService.precreate(id,subject,amount);
        if(response == null) throw new Exception("Get AlipayTradePrecreateResponse Fail");
        if(!"10000".equals(response.getCode()) || !response.isSuccess()) throw new Exception(response.getMsg());
        UserPaymentModel paymentModel = new UserPaymentModel();
        paymentModel.setId(id);
        paymentModel.setAmount(amount);
        paymentModel.setType(UserPaymentType.ALIPAY);
        paymentModel.setSubject(subject);
        paymentModel.setStatus(UserPaymentStatus.DEFAULT);
        paymentModel.setCreatedUserId(userId);
        paymentModel.setCreatedIp(ip);
        paymentModel.setCreatedTime(DateUtils.toLocalDateTime());
        paymentModel.setUpdatedUserId(userId);
        paymentModel.setUpdatedIp(ip);
        paymentModel.setUpdatedTime(DateUtils.toLocalDateTime());
        int result = insert(paymentModel);
        if(result != 1) throw new Exception("写入订单失败");
        return response;
    }

    @Override
    @CacheEvict(key = "#id")
    public UserPaymentStatus queryAlipay(String id, Long userId, Long ip) throws Exception {
        UserPaymentModel paymentModel = singleService.find(id);
        if(paymentModel == null) throw new Exception("订单不存在");
        if(!paymentModel.getStatus().equals(UserPaymentStatus.DEFAULT)) return paymentModel.getStatus();

        AlipayTradeQueryResponse response = alipayService.query(id);//通过alipayClient调用API，获得对应的response类
        if(response == null) throw new Exception("Get AlipayTradeQueryResponse Fail");
        if("TRADE_SUCCESS".equals(response.getTradeStatus()) || "TRADE_FINISHED".equals(response.getTradeStatus())){
            if(paymentModel.getStatus().equals(UserPaymentStatus.DEFAULT)){
                updateStatus(id,UserPaymentStatus.SUCCEED,userId,DateUtils.getTimestamp(),ip);
            }
            return UserPaymentStatus.SUCCEED;
        }
        if("TRADE_CLOSED".equals(response.getTradeStatus())){
            if(paymentModel.getStatus().equals(UserPaymentStatus.DEFAULT)){
                updateStatus(id,UserPaymentStatus.FAILED,userId,DateUtils.getTimestamp(),ip);
            }
            return UserPaymentStatus.FAILED;
        }
        return UserPaymentStatus.DEFAULT;
    }
}
