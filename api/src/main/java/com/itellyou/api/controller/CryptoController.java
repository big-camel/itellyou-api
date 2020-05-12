package com.itellyou.api.controller;

import com.itellyou.model.common.ResultModel;
import com.itellyou.util.CryptoUtils;
import com.itellyou.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@Validated
@RestController
public class CryptoController {

    @PostMapping("/crypto")
    public ResultModel crypto(@RequestParam @NotNull String action, @RequestParam @NotNull String text, @RequestParam @NotNull String pwd){
        try {
            if ("encrypt".equals(action)) return new ResultModel(CryptoUtils.encrypt(text, StringUtils.md5(pwd)));
            if ("decrypt".equals(action)) return new ResultModel(CryptoUtils.decrypt(text, StringUtils.md5(pwd)));
            return new ResultModel(0,"错误的action");
        }catch (Exception e){
            e.printStackTrace();
            return new ResultModel(0,"出错了");
        }
    }
}
