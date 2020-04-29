package com.itellyou.service.user.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.itellyou.dao.user.UserPaymentDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.*;
import com.itellyou.service.ali.AlipayService;
import com.itellyou.service.uid.UidGenerator;
import com.itellyou.service.user.UserBankService;
import com.itellyou.service.user.UserPaymentService;
import com.itellyou.util.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

@Service
public class UserPaymentServiceImpl implements UserPaymentService {

    private final UserPaymentDao paymentDao;
    private final UserBankService bankService;
    private final AlipayService alipayService;
    private final UidGenerator cachedUidGenerator;

    public UserPaymentServiceImpl(UserPaymentDao paymentDao, UserBankService bankService, AlipayService alipayService, UidGenerator cachedUidGenerator) {
        this.paymentDao = paymentDao;
        this.bankService = bankService;
        this.alipayService = alipayService;
        this.cachedUidGenerator = cachedUidGenerator;
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
    public PageModel<UserPaymentDetailModel> page(String id, UserPaymentStatus status, UserPaymentType type, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<UserPaymentDetailModel> data = search(id,status,type,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(id,status,type,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
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
                UserBankLogModel logModel = bankService.update(paymentModel.getAmount(), UserBankType.CASH,updatedUserId,type + "充值成功",UserBankLogType.PAY,id,updatedIp);
                if(logModel == null) throw new Exception("更新余额失败");
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
        paymentModel.setCreatedTime(DateUtils.getTimestamp());
        paymentModel.setUpdatedUserId(userId);
        paymentModel.setUpdatedIp(ip);
        paymentModel.setUpdatedTime(DateUtils.getTimestamp());
        int result = insert(paymentModel);
        if(result != 1) throw new Exception("写入订单失败");
        return response;
    }

    @Override
    public UserPaymentStatus queryAlipay(String id, Long userId, Long ip) throws Exception {
        UserPaymentModel paymentModel = getDetail(id);
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
