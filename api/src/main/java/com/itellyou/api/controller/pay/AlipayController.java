package com.itellyou.api.controller.pay;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.itellyou.api.handler.response.Result;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserPaymentStatus;
import com.itellyou.service.ali.AliPayServer;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Validated
@RestController
@RequestMapping("/pay")
public class AlipayController {

    private final AliPayServer aliPayServer;

    public AlipayController(AliPayServer aliPayServer) {
        this.aliPayServer = aliPayServer;
    }

    @PostMapping("/alipay")
    public Result alipayPrecreate(HttpServletRequest request, @MultiRequestBody double amount, UserInfoModel userModel){
        if(userModel == null) return new Result(401,"未登陆");
        try {
            AlipayTradePrecreateResponse response = aliPayServer.precreate("充值",amount,userModel.getId(), IPUtils.toLong(IPUtils.getClientIp(request)));
            return new Result().extend("id",response.getOutTradeNo()).extend("qr",response.getQrCode());
        }catch (Exception e){
            return new Result(500,e.getLocalizedMessage());
        }
    }

    @GetMapping("/alipay")
    public Result alipayQuery(HttpServletRequest request, @RequestParam String id, UserInfoModel userModel){
        if(userModel == null) return new Result(401,"未登陆");
        try {
            UserPaymentStatus status = aliPayServer.query(id,userModel.getId(), IPUtils.toLong(IPUtils.getClientIp(request)));
            return new Result().extend("status",status);
        }catch (Exception e){
            return new Result(500,e.getLocalizedMessage());
        }
    }
}
