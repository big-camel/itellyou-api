package com.itellyou.service.ali.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.itellyou.model.ali.AliPayConfigModel;
import com.itellyou.model.user.*;
import com.itellyou.service.ali.AlipayConfigService;
import com.itellyou.service.ali.AlipayService;
import com.itellyou.service.uid.UidGenerator;
import com.itellyou.service.user.UserPaymentService;
import com.itellyou.service.user.UserThirdLogService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

@Service
public class AlipayServiceImpl implements AlipayService {

    private final AlipayConfigService configServer;

    public AlipayServiceImpl(AlipayConfigService configServer, UserThirdLogService thirdLogService) {
        this.configServer = configServer;
    }

    @Override
    public AlipayClient getDefaultAlipayClient() throws Exception {
        AliPayConfigModel configModel = configServer.getDefault();
        if(configModel == null) throw new Exception("未启用支付宝接口配置");

        String format = "json";
        String type = "RSA2";
        String charset = Charset.defaultCharset().name();
        if(StringUtils.isEmpty(configModel.getAlipayCertPath()) || StringUtils.isEmpty(configModel.getPublicCertPath()) || StringUtils.isEmpty(configModel.getRootCertPath()) ){
            return new DefaultAlipayClient(configModel.getGateway(),configModel.getAppId(),configModel.getPrivateKey(),format,charset,configModel.getAlipayKey(),type);
        }
        //构造client
        String rootPath = System.getProperty("user.dir");
        CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
        certAlipayRequest.setServerUrl(configModel.getGateway());
        certAlipayRequest.setAppId(configModel.getAppId());
        certAlipayRequest.setPrivateKey(configModel.getPrivateKey());
        certAlipayRequest.setFormat(format);
        certAlipayRequest.setCharset(charset);
        certAlipayRequest.setSignType(type);
        certAlipayRequest.setCertPath(rootPath + configModel.getPublicCertPath());
        certAlipayRequest.setAlipayPublicCertPath(rootPath + configModel.getAlipayCertPath());
        certAlipayRequest.setRootCertPath(rootPath + configModel.getRootCertPath());
        return new DefaultAlipayClient(certAlipayRequest);
    }

    @Override
    public AlipayTradePrecreateResponse precreate(String orderId,String subject, double amount) throws Exception {
        AlipayClient alipayClient = getDefaultAlipayClient();
        AliPayConfigModel configModel = configServer.getDefault();
        if(configModel == null) throw new Exception("未启用支付宝接口配置");
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();//创建API对应的request类
        request.setNotifyUrl(configModel.getNotifyUrl());
        request.setBizContent("{" +
                "    \"out_trade_no\":\"" + orderId + "\"," +//商户订单号
                "    \"total_amount\":\"" + Math.abs(amount) + "\"," +
                "    \"subject\":\"" + subject + "\"," +
                "    \"store_id\":\"ITELLYOU\"," +
                "    \"timeout_express\":\"60m\"}");//订单允许的最晚付款时间
        return alipayClient.certificateExecute(request);
    }

    @Override
    public AlipayTradeQueryResponse query(String orderId) throws Exception {

        AlipayClient alipayClient = getDefaultAlipayClient();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();//创建API对应的request类
        request.setBizContent("{\"out_trade_no\":\"" + orderId + "\"}"); //设置业务参数
        return alipayClient.certificateExecute(request);//通过alipayClient调用API，获得对应的response类
    }

    @Override
    public AlipaySystemOauthTokenResponse getOAuthToken(String authCode) throws Exception {
        AlipayClient alipayClient = getDefaultAlipayClient();
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(authCode);
        request.setGrantType("authorization_code");
        return alipayClient.certificateExecute(request);
    }

    @Override
    public AlipayUserInfoShareResponse getUserInfo(String token) throws Exception {
        AlipayClient alipayClient = getDefaultAlipayClient();
        AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();
        return alipayClient.certificateExecute(request, token);
    }

    @Override
    public AlipayFundTransUniTransferResponse transfer(String orderId, String alipayUserId, String title, double amount) throws Exception {
        AlipayClient alipayClient = getDefaultAlipayClient();
        AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest ();
        request.setBizContent ( "{"   +
                "\"out_biz_no\":\"" + orderId + "\","   +
                "\"trans_amount\":" + amount + ","   +
                "\"product_code\":\"TRANS_ACCOUNT_NO_PWD\","   +
                "\"biz_scene\":\"DIRECT_TRANSFER\","   +
                "\"order_title\":\"" + title + "\","   +
                "\"payee_info\":{"   +
                "\"identity\":\"" + alipayUserId + "\","   +
                "\"identity_type\":\"ALIPAY_USER_ID\","   +
                "    },"   +
                "  }" );
        return alipayClient.certificateExecute (request);
    }

    @Override
    public boolean rsaCheckV1(Map<String, String> params,String signType) throws Exception {
        if(StringUtils.isEmpty(signType)) signType = "RSA2";
        AliPayConfigModel configModel = configServer.getDefault();
        if(configModel == null) throw new Exception("未启用支付宝接口配置");
        String rootPath = System.getProperty("user.dir");
        return AlipaySignature.rsaCertCheckV1(params,rootPath + configModel.getAlipayCertPath(),"UTF-8",signType);
    }

    @Override
    public boolean rsaCheckV1(Map<String, String> params) throws Exception {
        return rsaCheckV1(params,null);
    }
}
