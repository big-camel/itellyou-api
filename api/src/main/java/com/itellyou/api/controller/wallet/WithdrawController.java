package com.itellyou.api.controller.wallet;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.user.bank.UserWithdrawConfigService;
import com.itellyou.service.user.bank.UserWithdrawService;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/withdraw")
public class WithdrawController {

    private final UserWithdrawService withdrawService;
    private final UserWithdrawConfigService configService;

    public WithdrawController(UserWithdrawService withdrawService, UserWithdrawConfigService configService) {
        this.withdrawService = withdrawService;
        this.configService = configService;
    }

    @PostMapping("")
    public ResultModel withdraw(HttpServletRequest request, @MultiRequestBody double amount, UserInfoModel userModel){
        if(userModel == null) return new ResultModel(401,"未登陆");
        try {
            UserBankLogModel logModel = withdrawService.doWithdraw(userModel.getId(),amount,IPUtils.toLong(IPUtils.getClientIp(request)));
            if(logModel == null) throw new Exception("提现失败");
            return new ResultModel(logModel);
        }catch (Exception e){
            return new ResultModel(500,e.getLocalizedMessage());
        }
    }

    @GetMapping("/config")
    public ResultModel config(UserInfoModel userModel){
        if(userModel == null) return new ResultModel(401,"未登陆");
        return new ResultModel(configService.getDefault());
    }

    @GetMapping("/log")
    public ResultModel log(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new ResultModel(401,"未登陆");
        Map<String,String > order = new HashMap<>();
        order.put("created_time","desc");
        return new ResultModel(withdrawService.page(null,null,userModel.getId(),null,null,null,order,offset,limit));
    }
}
