package com.itellyou.api.controller.user;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.user.UserBankLogType;
import com.itellyou.model.user.UserBankType;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.user.UserBankLogService;
import com.itellyou.service.user.UserBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/bank")
public class BankController {

    private final UserBankService bankService;
    private final UserBankLogService bankLogService;

    @Autowired
    public BankController(UserBankService bankService, UserBankLogService bankLogService){
        this.bankService = bankService;
        this.bankLogService = bankLogService;
    }

    @GetMapping("")
    public Result me(UserInfoModel userModel){
        if(userModel == null){
            return new Result(401,"未登录");
        }
        return new Result(bankService.findByUserId(userModel.getId()));
    }

    @GetMapping("/log")
    public Result log(UserInfoModel userModel,@RequestParam(required = false) String type,@RequestParam(required = false,name = "data_type") String dataType,@RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null){
            return new Result(401,"未登录");
        }
        UserBankType bankType = null;
        UserBankLogType logType = null;
        try{
            if(type != null) bankType = UserBankType.valueOf(type.toUpperCase());
            if(logType != null ) logType = UserBankLogType.valueOf(dataType.toUpperCase());
        }catch (Exception e){ }
        Map<String,String > order = new HashMap<>();
        order.put("created_time","desc");
        return new Result(bankLogService.page(null,bankType,logType,userModel.getId(),null,null,null,order,offset,limit));
    }

}
