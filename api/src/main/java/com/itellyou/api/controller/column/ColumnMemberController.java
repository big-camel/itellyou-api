package com.itellyou.api.controller.column;


import com.itellyou.api.handler.response.Result;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.column.ColumnMemberDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.column.ColumnMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/column/member")
public class ColumnMemberController {

    private final ColumnMemberService memberService;

    @Autowired
    public ColumnMemberController(ColumnMemberService memberService){
        this.memberService = memberService;
    }

    @GetMapping("")
    public Result member(UserInfoModel userModel,@RequestParam @NotNull Long id){
        Long searchUserId = userModel == null ? null : userModel.getId();
        PageModel<ColumnMemberDetailModel> pageModel = memberService.page(id,null,searchUserId,null,null,null,null,null,null);
        return new Result(pageModel);
    }

}
