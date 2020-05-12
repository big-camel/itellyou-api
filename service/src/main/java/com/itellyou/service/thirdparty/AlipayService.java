package com.itellyou.service.thirdparty;

import com.alipay.api.AlipayClient;
import com.alipay.api.response.*;

import java.util.Map;

public interface AlipayService {

    /**
     * 获取alipay默认参数配置的Client实例
     * @return
     * @throws Exception
     */
    AlipayClient getDefaultAlipayClient() throws Exception;

    /**
     * 预创建alipay订单
     * @param orderId
     * @param subject
     * @return
     * @throws Exception
     */
    AlipayTradePrecreateResponse precreate(String orderId,String subject, double amount) throws Exception;

    /**
     * 查询alipay订单的支付状态
     * @param orderId
     * @return
     * @throws Exception
     */
    AlipayTradeQueryResponse query(String orderId) throws Exception;

    /**
     * 获取alipay令牌
     * @param authCode
     * @return
     * @throws Exception
     */
    AlipaySystemOauthTokenResponse getOAuthToken(String authCode) throws Exception;

    /**
     * 获取alipay用户信息
     * @param token getOAuthToken 获取到的 access_token
     * @return
     */
    AlipayUserInfoShareResponse getUserInfo(String token) throws Exception;

    AlipayFundTransUniTransferResponse transfer(String orderId,String alipayUserId, String title,double amount) throws Exception;

    boolean rsaCheckV1(Map<String,String> params,String signType) throws Exception;

    boolean rsaCheckV1(Map<String,String> params) throws Exception;
}
