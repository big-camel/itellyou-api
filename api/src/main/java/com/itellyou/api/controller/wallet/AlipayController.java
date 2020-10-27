package com.itellyou.api.controller.wallet;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserPaymentModel;
import com.itellyou.model.user.UserPaymentStatus;
import com.itellyou.service.thirdparty.AlipayService;
import com.itellyou.service.user.bank.UserPaymentSearchService;
import com.itellyou.service.user.bank.UserPaymentService;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;


@Validated
@RestController
@RequestMapping("/pay")
public class AlipayController {

    private final UserPaymentService paymentService;
    private final UserPaymentSearchService paymentSearchService;
    private final AlipayService alipayService;

    public AlipayController(UserPaymentService paymentService, UserPaymentSearchService paymentSearchService, AlipayService alipayService) {
        this.paymentService = paymentService;
        this.paymentSearchService = paymentSearchService;
        this.alipayService = alipayService;
    }

    @PostMapping("/alipay")
    public ResultModel alipayPrecreate(HttpServletRequest request, @MultiRequestBody double amount, UserInfoModel userModel){
        if(userModel == null) return new ResultModel(401,"未登陆");
        try {
            AlipayTradePrecreateResponse response = paymentService.preCreateAlipay("支付宝充值",amount,userModel.getId(), IPUtils.toLong(IPUtils.getClientIp(request)));
            return new ResultModel().extend("id",response.getOutTradeNo()).extend("qr",response.getQrCode());
        }catch (Exception e){
            return new ResultModel(500,e.getLocalizedMessage());
        }
    }

    @GetMapping("/alipay")
    public ResultModel alipayQuery(HttpServletRequest request, @RequestParam String id, UserInfoModel userModel){
        if(userModel == null) return new ResultModel(401,"未登陆");
        try {
            UserPaymentStatus status = paymentService.queryAlipay(id,userModel.getId(), IPUtils.toLong(IPUtils.getClientIp(request)));
            return new ResultModel().extend("status",status);
        }catch (Exception e){
            return new ResultModel(500,e.getLocalizedMessage());
        }
    }

    @GetMapping("/alipay/callback")
    public void callback(HttpServletRequest request,HttpServletResponse response, @RequestParam Map<String,String> params){
        String result = "failure";

        try {
            if(params != null){
                boolean signVerified = alipayService.rsaCheckV1(params);
                if(signVerified){
                    String orderId = params.get("out_trade_no");
                    UserPaymentModel paymentModel = paymentSearchService.getDetail(orderId);
                    if(paymentModel != null && paymentModel.getStatus().equals(UserPaymentStatus.DEFAULT)){
                        paymentService.queryAlipay(orderId,paymentModel.getCreatedUserId(),IPUtils.toLong(IPUtils.getClientIp(request)));
                    }
                    result = "success";
                }
            }

            ServletOutputStream out = response.getOutputStream();
            OutputStreamWriter ow = new OutputStreamWriter(out, "UTF-8");
            ow.write(result);
            ow.flush();
            ow.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("/log")
    public ResultModel log(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new ResultModel(401,"未登陆");
        Map<String,String > order = new HashMap<>();
        order.put("created_time","desc");
        return new ResultModel(paymentSearchService.page(null,null,null,userModel.getId(),null,null,null,order,offset,limit));
    }
}
