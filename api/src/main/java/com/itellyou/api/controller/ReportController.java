package com.itellyou.api.controller;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.ReportAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.sys.ReportService;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/post")
    public ResultModel post(UserInfoModel userModel, HttpServletRequest request, @MultiRequestBody @NotBlank String action, @MultiRequestBody @NotBlank String type, @MultiRequestBody @NotBlank String description, @MultiRequestBody @NotNull Long id){
        if(userModel == null)return new ResultModel(403,"未登陆");
        String clientIp = IPUtils.getClientIp(request);
        ReportAction reportAction = null;
        EntityType entityType = null;
        try {
            reportAction = ReportAction.valueOf(action.toUpperCase());
            entityType = EntityType.valueOf(type.toUpperCase());
        }catch (Exception e){
            return new ResultModel("非法的参数");
        }
        try{
            int result = reportService.insert(reportAction,entityType,id,description,userModel.getId(),IPUtils.toLong(clientIp));
            if(result != 1) return new ResultModel(0,"举报失败");
            return new ResultModel();
        }catch (Exception e){
            return new ResultModel(500,e.getMessage());
        }

    }
}
