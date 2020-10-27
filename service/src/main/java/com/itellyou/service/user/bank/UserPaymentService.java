package com.itellyou.service.user.bank;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.itellyou.model.user.UserPaymentModel;
import com.itellyou.model.user.UserPaymentStatus;

public interface UserPaymentService {
    int insert(UserPaymentModel model);

    /**
     * 更新订单状态
     * @param id
     * @param status
     * @param updatedUserId
     * @param updatedTime
     * @param updatedIp
     * @return
     * @throws Exception
     */
    int updateStatus(String id , UserPaymentStatus status, Long updatedUserId, Long updatedTime, Long updatedIp) throws Exception;

    /**
     * 预创建alipay订单
     * @param subject
     * @param amount
     * @param userId
     * @param ip
     * @return
     * @throws Exception
     */
    AlipayTradePrecreateResponse preCreateAlipay(String subject, double amount, Long userId, Long ip) throws Exception;

    /**
     * 查询alipay订单的支付状态
     * @param id
     * @param userId
     * @param ip
     * @return
     * @throws Exception
     */
    UserPaymentStatus queryAlipay(String id, Long userId, Long ip) throws Exception;
}
