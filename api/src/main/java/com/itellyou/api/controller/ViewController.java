package com.itellyou.api.controller;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.common.ViewInfoModel;
import com.itellyou.service.common.ViewService;
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
@RequestMapping("/view")
public class ViewController {

    private final ViewService viewService;

    @Autowired
    public ViewController(ViewService viewService){
        this.viewService = viewService;
    }
    @GetMapping("/list")
    public ResultModel list(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit) {
        if(userModel == null) return new ResultModel(401,"未登陆");
        Map<String,String> order = new HashMap<>();
        order.put("updated_time","desc");
        PageModel<ViewInfoModel> data = viewService.page(null,userModel.getId(),null,null,null,null,null,null,null,order,offset,limit);
        return new ResultModel(data);
    }
}
