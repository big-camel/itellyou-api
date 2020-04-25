package com.itellyou.api.controller;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.geetest.GeetestClientTypeEnum;
import com.itellyou.model.geetest.GeetestModel;
import com.itellyou.service.geetest.GeetestService;
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
    public Result init(HttpServletRequest request, @RequestParam(required = false) String mode, UserInfoModel userModel){
        if(mode == null){
            mode = "unknow";
        }
        String clientIp = IPUtils.getClientIp(request);
        Long userId = userModel == null ? 0L : userModel.getId();
        GeetestModel geetestModel = geetestService.init(mode,userId, GeetestClientTypeEnum.WEB,clientIp);
        if(geetestModel == null) return new Result(0,"Geetest 实例化失败");
        return new Result(geetestModel);
    }
}
