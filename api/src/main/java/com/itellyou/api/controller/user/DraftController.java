package com.itellyou.api.controller.user;

import com.itellyou.model.common.ResultModel;
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
    public ResultModel draft(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<UserDraftDetailModel> pageData = draftService.page(null,null,null,userModel.getId(),null,null,null,order,offset,limit);
        return new ResultModel(pageData);
    }

    @DeleteMapping("")
    public ResultModel draft(UserInfoModel userModel, @MultiRequestBody @NotBlank String type, @MultiRequestBody @NotNull Long key){
        int result = draftService.delete(userModel.getId(), EntityType.valueOf(type.toUpperCase()),key);
        if(result == 1) return new ResultModel();
        return new ResultModel(0,"删除错误");
    }
}
