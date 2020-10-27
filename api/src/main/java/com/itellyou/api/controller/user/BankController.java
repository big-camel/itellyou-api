package com.itellyou.api.controller.user;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserBankConfigModel;
import com.itellyou.model.user.UserBankType;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.user.bank.UserBankConfigService;
import com.itellyou.service.user.bank.UserBankLogSearchService;
import com.itellyou.service.user.bank.UserBankService;
import com.itellyou.util.Params;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/bank")
public class BankController {

    private final UserBankService bankService;
    private final UserBankConfigService bankConfigService;
    private final UserBankLogSearchService logSearchService;

    @Autowired
    public BankController(UserBankService bankService, UserBankConfigService bankConfigService, UserBankLogSearchService logSearchService){
        this.bankService = bankService;
        this.logSearchService = logSearchService;
        this.bankConfigService = bankConfigService;
    }

    @GetMapping("")
    public ResultModel me(UserInfoModel userModel){
        return new ResultModel(bankService.findByUserId(userModel.getId()));
    }

    @GetMapping("/log")
    public ResultModel log(UserInfoModel userModel, @RequestParam(required = false) String type, @RequestParam(required = false,name = "action") String action, @RequestParam(required = false,name = "data_type") String dataType, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        UserBankType bankType = null;
        EntityAction bankAction = null;
        EntityType bankDataType = null;
        try{
            if(type != null) bankType = UserBankType.valueOf(type.toUpperCase());
            if(action != null ) bankAction = EntityAction.valueOf(action.toUpperCase());
            if(dataType != null ) bankDataType = EntityType.valueOf(dataType.toUpperCase());
        }catch (Exception e){ }
        Map<String,String > order = new HashMap<>();
        order.put("created_time","desc");
        return new ResultModel(logSearchService.page(null,bankType,bankAction,bankDataType,null,userModel.getId(),null,null,null,order,offset,limit));
    }

    @GetMapping("/config/{type:credit|score}")
    public ResultModel config(@PathVariable String type){
        UserBankType bankType;
        try{
            bankType = UserBankType.valueOf(type.toUpperCase());
        }catch (Exception e){
            return new ResultModel(500,"错误的type");
        }
        return new ResultModel(bankConfigService.findByType(bankType));
    }

    @PostMapping("/config/{valType:credit|score}")
    public ResultModel updateConfig(@PathVariable String valType, @MultiRequestBody(value = "action") String actionString, @MultiRequestBody(value = "type") String typeString, @MultiRequestBody(required = false,parseAllFields = true) Map args){
        Params params = new Params(args);
        UserBankType bankType = null;
        EntityAction action = null;
        EntityType type = null;
        try{
            if(valType != null) bankType = UserBankType.valueOf(valType.toUpperCase());
            if(actionString != null ) action = EntityAction.valueOf(actionString.toUpperCase());
            if(typeString != null ) type = EntityType.valueOf(typeString.toUpperCase());
        }catch (Exception e){
            return new ResultModel(500,"错误的type");
        }
        Integer targeterStep = params.getOrDefault("targeter_step",Integer.class,0);
        Integer createrStep = params.getOrDefault("creater_step",Integer.class,0);
        Integer createrMinScore = params.getOrDefault("creater_min_score",Integer.class,0);
        Integer targeterCountOfDay = params.getOrDefault("targeter_count_of_day",Integer.class,0);
        Integer targeterTotalOfDay = params.getOrDefault("targeter_total_of_day",Integer.class,0);
        Integer targeterCountOfWeek = params.getOrDefault("targeter_count_of_week",Integer.class,0);
        Integer targeterTotalOfWeek = params.getOrDefault("targeter_total_of_week",Integer.class,0);
        Integer targeterCountOfMonth = params.getOrDefault("targeter_count_of_month",Integer.class,0);
        Integer targeterTotalOfMonth = params.getOrDefault("targeter_total_of_month",Integer.class,0);
        Integer createrCountOfDay = params.getOrDefault("creater_count_of_day",Integer.class,0);
        Integer createrTotalOfDay = params.getOrDefault("creater_total_of_day",Integer.class,0);
        Integer createrCountOfWeek = params.getOrDefault("creater_count_of_week",Integer.class,0);
        Integer createrTotalOfWeek = params.getOrDefault("creater_total_of_week",Integer.class,0);
        Integer createrCountOfMonth = params.getOrDefault("creater_count_of_month",Integer.class,0);
        Integer createrTotalOfMonth = params.getOrDefault("creater_total_of_month",Integer.class,0);
        String targeterRemark = params.getOrDefault("targeter_remark","");
        String createrRemark = params.getOrDefault("creater_remark","");
        Boolean onlyOnce = params.getOrDefault("only_once",Boolean.class,false);

        UserBankConfigModel configModel = new UserBankConfigModel();
        configModel.setBankType(bankType);
        configModel.setAction(action);
        configModel.setBankType(bankType);
        configModel.setType(type);
        configModel.setTargeterStep(targeterStep);
        configModel.setCreaterStep(createrStep);
        configModel.setCreaterMinScore(createrMinScore);
        configModel.setTargeterCountOfDay(targeterCountOfDay);
        configModel.setTargeterTotalOfDay(targeterTotalOfDay);
        configModel.setTargeterCountOfWeek(targeterCountOfWeek);
        configModel.setTargeterTotalOfWeek(targeterTotalOfWeek);
        configModel.setTargeterCountOfMonth(targeterCountOfMonth);
        configModel.setTargeterTotalOfMonth(targeterTotalOfMonth);
        configModel.setCreaterCountOfDay(createrCountOfDay);
        configModel.setCreaterTotalOfDay(createrTotalOfDay);
        configModel.setCreaterCountOfWeek(createrCountOfWeek);
        configModel.setCreaterTotalOfWeek(createrTotalOfWeek);
        configModel.setCreaterCountOfMonth(createrCountOfMonth);
        configModel.setCreaterTotalOfMonth(createrTotalOfMonth);
        configModel.setTargeterRemark(targeterRemark);
        configModel.setCreaterRemark(createrRemark);
        configModel.setOnlyOnce(onlyOnce);
        int result = bankConfigService.update(configModel);
        if(result != 1) return new ResultModel(500,"更新错误");
        return new ResultModel();
    }
}
