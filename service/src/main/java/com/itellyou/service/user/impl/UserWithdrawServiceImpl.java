package com.itellyou.service.user.impl;

import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.itellyou.dao.user.UserWithdrawConfigDao;
import com.itellyou.dao.user.UserWithdrawDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.*;
import com.itellyou.service.ali.AlipayService;
import com.itellyou.service.uid.UidGenerator;
import com.itellyou.service.user.UserBankService;
import com.itellyou.service.user.UserThirdAccountService;
import com.itellyou.service.user.UserWithdrawService;
import com.itellyou.util.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class UserWithdrawServiceImpl implements UserWithdrawService {

    private final UidGenerator cachedUidGenerator;
    private final UserWithdrawDao withdrawDao;
    private final UserBankService bankService;
    private final UserWithdrawConfigDao configDao;
    private final AlipayService alipayService;
    private final UserThirdAccountService accountService;

    public UserWithdrawServiceImpl(UidGenerator cachedUidGenerator, UserWithdrawDao withdrawDao, UserBankService bankService, UserWithdrawConfigDao configDao, AlipayService alipayService, UserThirdAccountService accountService) {
        this.cachedUidGenerator = cachedUidGenerator;
        this.withdrawDao = withdrawDao;
        this.bankService = bankService;
        this.configDao = configDao;
        this.alipayService = alipayService;
        this.accountService = accountService;
    }

    @Override
    public int insert(UserWithdrawModel model) {
        return withdrawDao.insert(model);
    }

    @Override
    public List<UserWithdrawDetailModel> search(String id, UserPaymentStatus status, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return withdrawDao.search(id,status,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(String id, UserPaymentStatus status, Long userId, Long beginTime, Long endTime, Long ip) {
        return withdrawDao.count(id,status,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<UserWithdrawDetailModel> page(String id, UserPaymentStatus status, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<UserWithdrawDetailModel> data = search(id,status,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(id,status,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public UserWithdrawDetailModel getDetail(String id) {
        List<UserWithdrawDetailModel> list = search(id,null,null,null,null,null,null,null,null);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public double getLockedCash(Long userId) {
        return withdrawDao.getLockedCash(userId);
    }

    @Override
    @Transactional
    public int updateStatus(String id, UserPaymentStatus status, Long updatedUserId, Long updatedTime, Long updatedIp) throws Exception {
        try {
            int result = withdrawDao.updateStatus(id, status, updatedUserId, updatedTime, updatedIp);
            if(result == 1 && status.equals(UserPaymentStatus.FAILED)){
                UserWithdrawModel withdrawModel = getDetail(id);
                BigDecimal bigAmount = new BigDecimal(withdrawModel.getAmount());
                bigAmount.add(new BigDecimal(withdrawModel.getCommissionCharge()));
                UserBankLogModel logModel = bankService.update(bigAmount.doubleValue(), UserBankType.CASH,withdrawModel.getCreatedUserId(),  "提现失败，资金返还",UserBankLogType.WITHDRAW,id,updatedIp);
                if(logModel == null) throw new Exception("更新余额失败");
            }
            return result;
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @Transactional
    public UserBankLogModel doWithdraw(Long userId, double amount, Long ip) throws Exception {
        try {
            amount = Math.abs(amount);
            UserWithdrawConfigModel configModel = configDao.getDefault();
            if(configModel == null) throw new Exception("未配置提现参数");
            if(amount < configModel.getMin() || amount > configModel.getMax()) throw new Exception(
                    new StringBuilder("取现金额不能小于 ").append(configModel.getMin()).append(" 元").
                            append(" , 大于 ").
                            append(configModel.getMax()).append(" 元").toString());

            UserWithdrawModel withdrawModel = new UserWithdrawModel();
            String id = new StringBuilder("WA").append(cachedUidGenerator.getUID()).toString();
            BigDecimal bigAmount = new BigDecimal(amount);
            BigDecimal bigRate = new BigDecimal(configModel.getRate());
            BigDecimal commissionCharge = bigAmount.multiply(bigRate);
            commissionCharge = commissionCharge.divide(new BigDecimal(100));
            bigAmount = bigAmount.subtract(commissionCharge);
            withdrawModel.setId(id);
            withdrawModel.setAmount(bigAmount.doubleValue());
            withdrawModel.setCommissionCharge(commissionCharge.doubleValue());
            withdrawModel.setCreatedIp(ip);
            withdrawModel.setCreatedTime(DateUtils.getTimestamp());
            withdrawModel.setCreatedUserId(userId);
            withdrawModel.setStatus(UserPaymentStatus.DEFAULT);
            withdrawModel.setSubject("提现");
            withdrawModel.setUpdatedIp(ip);
            withdrawModel.setUpdatedTime(DateUtils.getTimestamp());
            withdrawModel.setUpdatedUserId(userId);
            int result = this.insert(withdrawModel);
            if(result != 1) throw new Exception("提现失败");
            UserBankLogModel logModel = bankService.update(-bigAmount.doubleValue(), UserBankType.CASH,userId,  "提现到支付宝",UserBankLogType.WITHDRAW,id,ip);
            if(logModel == null) throw new Exception("扣除余额失败");
            UserBankLogModel feeLog = bankService.update(-commissionCharge.doubleValue(), UserBankType.CASH,userId,  "提现手续费",UserBankLogType.FEE,id,ip);
            if(feeLog == null) throw new Exception("手续费扣除失败");

            if(amount <= configModel.getAuto()){
                result = this.doWithdraw(id,userId,ip);
                if(result != 1) throw new Exception("提现失败");
            }
            return logModel;
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    public int doWithdraw(String withdrawId, Long updatedUserId, Long updatedIp) throws Exception {
        UserWithdrawModel withdrawModel = getDetail(withdrawId);
        if(withdrawModel == null) throw new Exception("提现记录不存在");
        Map<UserThirdAccountType,UserThirdAccountModel> accountModelMap = accountService.searchByUserId(withdrawModel.getCreatedUserId());
        if(accountModelMap == null || !accountModelMap.containsKey(UserThirdAccountType.ALIPAY)) throw new Exception("支付宝账户未绑定");
        if(withdrawModel.getStatus().equals(UserPaymentStatus.DEFAULT)){
            AlipayFundTransUniTransferResponse response = alipayService.transfer(withdrawModel.getId(),accountModelMap.get(UserThirdAccountType.ALIPAY).getKey(),"支付宝提现",withdrawModel.getAmount());
            return updateStatus(withdrawId,response.isSuccess() ? UserPaymentStatus.SUCCEED : UserPaymentStatus.FAILED,updatedUserId,DateUtils.getTimestamp(),updatedIp);
        }
        throw new Exception("订单状态不正确");
    }
}
