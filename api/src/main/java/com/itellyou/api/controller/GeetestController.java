package com.itellyou.api.controller;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.thirdparty.GeetestClientTypeEnum;
import com.itellyou.model.thirdparty.GeetestModel;
import com.itellyou.service.thirdparty.GeetestService;
import com.itellyou.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class GeetestController {

    private final GeetestService geetestService;

    @Autowired
    public GeetestController(GeetestService geetestService){
        this.geetestService = geetestService;
    }

    @GetMapping("/geetest")
    public ResultModel init(HttpServletRequest request, @RequestParam(required = false) String mode, UserInfoModel userModel){
        if(mode == null){
            mode = "unknow";
        }
        String clientIp = IPUtils.getClientIp(request);
        Long userId = userModel == null ? 0L : userModel.getId();
        GeetestModel geetestModel = geetestService.init(mode,userId, GeetestClientTypeEnum.WEB,clientIp);
        if(geetestModel == null) return new ResultModel(0,"Geetest 实例化失败");
        return new ResultModel(geetestModel);
    }
}
