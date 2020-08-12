package com.itellyou.api.controller.user;

import com.itellyou.model.software.SoftwareDetailModel;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.software.SoftwareSearchService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/user/software")
public class UserSoftwareController {

    private final SoftwareSearchService searchService;

    public UserSoftwareController(SoftwareSearchService searchService){
        this.searchService = searchService;
    }

    @GetMapping("")
    public ResultModel draft(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new ResultModel(401,"未登陆");
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<SoftwareDetailModel> pageData = searchService.page(null,"draft",null,userModel.getId(),userModel.getCreatedUserId(),false,false,false,true,null,null,null,null,null,null,null,null,null,null,null,null,order,offset,limit);
        return new ResultModel(pageData);
    }
}
