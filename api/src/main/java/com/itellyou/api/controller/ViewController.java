package com.itellyou.api.controller;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.common.ViewDetailModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.common.ViewSearchService;
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

    private final ViewSearchService searchService;

    public ViewController(ViewSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/list")
    public ResultModel list(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit) {
        Map<String,String> order = new HashMap<>();
        order.put("updated_time","desc");
        PageModel<ViewDetailModel> data = searchService.page(null,userModel.getId(),null,null,null,null,null,null,null,order,offset,limit);
        return new ResultModel(data);
    }
}
