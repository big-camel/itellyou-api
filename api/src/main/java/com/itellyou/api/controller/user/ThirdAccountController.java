package com.itellyou.api.controller.user;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.thirdparty.ThirdAccountType;
import com.itellyou.service.thirdparty.ThirdAccountService;
import com.itellyou.util.IPUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Validated
@RestController
@RequestMapping("/third_account")
public class ThirdAccountController {

    private final ThirdAccountService accountService;

    public ThirdAccountController(ThirdAccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("")
    public ResultModel get(UserInfoModel userModel){
        return new ResultModel(accountService.searchByUserId(userModel.getId()));
    }

    @DeleteMapping("")
    public ResultModel delete(UserInfoModel userModel, HttpServletRequest request, @RequestParam String type){
        try {
            ThirdAccountType accountType = ThirdAccountType.valueOf(type.toUpperCase());
            int result = accountService.deleteByUserIdAndType(userModel.getId(), accountType, IPUtils.toLong(IPUtils.getClientIp(request)));
            if (result == 1) return new ResultModel();
            return new ResultModel(500, "解绑失败");
        }catch (Exception e){
            return new ResultModel(500,e.getLocalizedMessage());
        }
    }
}
