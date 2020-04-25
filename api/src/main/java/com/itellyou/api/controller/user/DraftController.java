package com.itellyou.api.controller.user;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserDraftDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/user/draft")
public class DraftController {

    private final UserDraftService draftService;

    public DraftController(UserDraftService draftService){
        this.draftService = draftService;
    }

    @GetMapping("")
    public Result draft(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new Result(401,"未登陆");
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<UserDraftDetailModel> pageData = draftService.page(null,null,null,userModel.getId(),null,null,null,order,offset,limit);
        return new Result(pageData);
    }

    @DeleteMapping("")
    public Result draft(UserInfoModel userModel, @MultiRequestBody @NotBlank String type, @MultiRequestBody @NotNull Long key){
        if(userModel == null) return new Result(401,"未登陆");

        int result = draftService.delete(userModel.getId(), EntityType.valueOf(type.toUpperCase()),key);
        if(result == 1) return new Result();
        return new Result(0,"删除错误");
    }
}
