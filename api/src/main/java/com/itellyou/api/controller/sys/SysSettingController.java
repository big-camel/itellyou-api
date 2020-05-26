package com.itellyou.api.controller.sys;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.SysSettingModel;
import com.itellyou.service.sys.SysSettingService;
import com.itellyou.util.Params;
import com.itellyou.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/system/setting")
public class SysSettingController {

    private final SysSettingService settingService;

    public SysSettingController(SysSettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("")
    public ResultModel get(){
        return new ResultModel(settingService.findByDefault());
    }

    @PostMapping("")
    public ResultModel edit(@RequestBody Map<String,Object> params){
        try {
            SysSettingModel settingModel = new SysSettingModel();
            String key = Params.getOrDefault(params, "key", null);
            if (StringUtils.isEmpty(key)) return new ResultModel(500, "错误的key");
            String name = Params.getOrDefault(params, "name", null);
            String logo = Params.getOrDefault(params, "logo", null);
            String icpText = Params.getOrDefault(params, "icp_text", null);
            String copyright = Params.getOrDefault(params, "copyright", null);
            String companyName = Params.getOrDefault(params, "company_name", null);
            String userAgreementLink = Params.getOrDefault(params, "user_agreement_link", null);
            String footerScripts = Params.getOrDefault(params, "footer_scripts", null);
            settingModel.setKey(key);
            settingModel.setName(name);
            settingModel.setLogo(logo);
            settingModel.setIcpText(icpText);
            settingModel.setCopyright(copyright);
            settingModel.setCompanyName(companyName);
            settingModel.setUserAgreementLink(userAgreementLink);
            settingModel.setFooterScripts(footerScripts);
            settingService.updateByKey(settingModel);
        }catch (Exception e){
        }
        return new ResultModel();
    }
}
