package com.itellyou.service.ali.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.itellyou.model.ali.AliPayConfigModel;
import com.itellyou.model.user.UserPaymentModel;
import com.itellyou.model.user.UserPaymentStatus;
import com.itellyou.model.user.UserPaymentType;
import com.itellyou.service.ali.AliPayConfigServer;
import com.itellyou.service.ali.AliPayServer;
import com.itellyou.service.uid.UidGenerator;
import com.itellyou.service.user.UserPaymentServer;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

@Service
public class AliPayServerImpl implements AliPayServer {

    private final AliPayConfigServer configServer;
    private final UidGenerator cachedUidGenerator;
    private final UserPaymentServer paymentServer;

    public AliPayServerImpl(AliPayConfigServer configServer, UidGenerator cachedUidGenerator, UserPaymentServer paymentServer) {
        this.configServer = configServer;
        this.cachedUidGenerator = cachedUidGenerator;
        this.paymentServer = paymentServer;
    }

    @Override
    public AlipayClient getDefaultAlipayClient() throws Exception {
        AliPayConfigModel configModel = configServer.getDefault();
        if(configModel == null) throw new Exception("未启用支付宝支付");

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
    public AlipayTradePrecreateResponse precreate(String subject, double amount, Long userId, Long ip) throws Exception {
        AlipayClient alipayClient = getDefaultAlipayClient();
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();//创建API对应的request类
        String id = "AP" + cachedUidGenerator.getUID();
        request.setBizContent("{" +
                "    \"out_trade_no\":\"" + id + "\"," +//商户订单号
                "    \"total_amount\":\"" + Math.abs(amount) + "\"," +
                "    \"subject\":\"" + subject + "\"," +
                "    \"store_id\":\"ITELLYOU\"," +
                "    \"timeout_express\":\"60m\"}");//订单允许的最晚付款时间
        AlipayTradePrecreateResponse response = alipayClient.certificateExecute(request);
        if(response == null) throw new Exception("Get AlipayTradePrecreateResponse Fail");
        if(!"10000".equals(response.getCode())) throw new Exception(response.getMsg());
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
        int result = paymentServer.insert(paymentModel);
        if(result != 1) throw new Exception("写入订单失败");
        return response;
    }

    @Override
    public UserPaymentStatus query(String id,Long userId,Long ip) throws Exception {
        UserPaymentModel paymentModel = paymentServer.getDetail(id);
        if(paymentModel == null) throw new Exception("订单不存在");
        if(!paymentModel.getStatus().equals(UserPaymentStatus.DEFAULT)) return paymentModel.getStatus();

        AlipayClient alipayClient = getDefaultAlipayClient();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();//创建API对应的request类
        request.setBizContent("{\"out_trade_no\":\"" + id + "\"}"); //设置业务参数
        AlipayTradeQueryResponse response = alipayClient.certificateExecute(request);//通过alipayClient调用API，获得对应的response类
        if(response == null) throw new Exception("Get AlipayTradeQueryResponse Fail");
        if("TRADE_SUCCESS".equals(response.getTradeStatus()) || "TRADE_FINISHED".equals(response.getTradeStatus())){
            if(paymentModel.getStatus().equals(UserPaymentStatus.DEFAULT)){
                paymentServer.updateStatus(id,UserPaymentStatus.SUCCEED,userId,DateUtils.getTimestamp(),ip);
            }
            return UserPaymentStatus.SUCCEED;
        }
        if("TRADE_CLOSED".equals(response.getTradeStatus())){
            if(paymentModel.getStatus().equals(UserPaymentStatus.DEFAULT)){
                paymentServer.updateStatus(id,UserPaymentStatus.FAILED,userId,DateUtils.getTimestamp(),ip);
            }
            return UserPaymentStatus.FAILED;
        }
        return UserPaymentStatus.DEFAULT;
    }
}
