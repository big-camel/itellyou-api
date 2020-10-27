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
    public ResultModel edit(@RequestBody Map<String,Object> args){
        try {
            Params params = new Params(args);
            SysSettingModel settingModel = new SysSettingModel();
            String key = params.get( "key");
            if (StringUtils.isEmpty(key)) return new ResultModel(500, "错误的key");
            String name = params.get( "name");
            String logo = params.get( "logo");
            String icpText = params.get( "icp_text");
            String copyright = params.get( "copyright");
            String companyName = params.get( "company_name");
            String userAgreementLink = params.get( "user_agreement_link");
            String footerScripts = params.get( "footer_scripts");
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
