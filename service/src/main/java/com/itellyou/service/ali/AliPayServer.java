package com.itellyou.service.ali;

import com.alipay.api.AlipayClient;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.itellyou.model.user.UserPaymentStatus;

public interface AliPayServer {

    AlipayClient getDefaultAlipayClient() throws Exception;

    AlipayTradePrecreateResponse precreate(String subject, double amount, Long userId, Long ip) throws Exception;

    UserPaymentStatus query(String id, Long userId, Long ip) throws Exception;
}
