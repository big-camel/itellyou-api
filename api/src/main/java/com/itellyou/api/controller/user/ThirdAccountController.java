package com.itellyou.api.controller.user;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserThirdAccountModel;
import com.itellyou.model.user.UserThirdAccountType;
import com.itellyou.service.user.UserThirdAccountService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/third_account")
public class ThirdAccountController {

    private final UserThirdAccountService accountService;

    public ThirdAccountController(UserThirdAccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("")
    public Result get(UserInfoModel userModel){
        if(userModel == null){
            return new Result(401,"未登录");
        }
        Map<UserThirdAccountType,UserThirdAccountModel> data = accountService.searchByUserId(userModel.getId());
        return new Result(data);
    }

    @DeleteMapping("")
    public Result delete(UserInfoModel userModel,@RequestParam String type){
        if(userModel == null){
            return new Result(401,"未登录");
        }
        try {
            UserThirdAccountType accountType = UserThirdAccountType.valueOf(type.toUpperCase());
            int result = accountService.deleteByUserIdAndType(userModel.getId(), accountType);
            if (result == 1) return new Result();
            return new Result(500, "解绑失败");
        }catch (Exception e){
            return new Result(500,e.getLocalizedMessage());
        }
    }
}
