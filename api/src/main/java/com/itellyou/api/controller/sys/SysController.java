package com.itellyou.api.controller.sys;

import com.alibaba.fastjson.JSON;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.SysAdDetailModel;
import com.itellyou.model.sys.SysPermissionModel;
import com.itellyou.model.sys.SysPermissionPlatform;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.sys.SysAdSearchService;
import com.itellyou.service.sys.SysLinkService;
import com.itellyou.service.sys.SysPermissionService;
import com.itellyou.service.sys.SysSettingService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Validated
@RestController
@RequestMapping("/system")
public class SysController {

    private final SysPermissionService permissionService;
    private final SysSettingService settingService;
    private final SysLinkService linkService;
    private final UserSearchService userSearchService;
    private final SysAdSearchService adSearchService;

    public SysController(SysPermissionService permissionService, SysSettingService settingService, SysLinkService linkService, UserSearchService userSearchService, SysAdSearchService adSearchService) {
        this.userSearchService = userSearchService;
        this.permissionService = permissionService;
        this.settingService = settingService;
        this.linkService = linkService;
        this.adSearchService = adSearchService;
    }

    @GetMapping("/init")
    public ResultModel init(HttpServletRequest request, UserInfoModel userModel, @RequestParam(required = false) String p) throws InstantiationException, IllegalAccessException {
        Map<String,Object> dataMap = new HashMap<>();
        if(userModel != null){
            UserDetailModel detailModel = userSearchService.find(userModel.getId(),null);
            SysPermissionPlatform platform = SysPermissionPlatform.WEB;
            try{
                if(StringUtils.isNotEmpty(p)){
                    platform = SysPermissionPlatform.valueOf(p.toUpperCase());
                }
            }catch (Exception e){}
            List<SysPermissionModel> permissionModels = permissionService.search(userModel.getId(), platform);
            ResultModel resultModel = new ResultModel(detailModel,"base","bank","rank").
                    extend("access",permissionModels);
            dataMap.put("user", JSON.parse(resultModel.toJsonString()));
        }
        dataMap.put("setting",settingService.findByDefault());
        dataMap.put("link",linkService.page(null,null,null,null,null,null,null,null,null,0,9999));
        //获取IP所在地
        IPUtils.RegionModel regionModel = IPUtils.getRegion(request);
        if(regionModel == null) regionModel = new IPUtils.RegionModel("中国","","","","");
        //根据所在地获取广告配置
        boolean isChina = regionModel.getCountry().equals("中国") && !Arrays.asList("香港","台湾","澳门").contains(regionModel.getCity());
        SysAdDetailModel adDetailModel = isChina ? adSearchService.findByEnabledCn(true) : adSearchService.findByEnabledForeign(true);
        dataMap.put("ad",adDetailModel);
        return new ResultModel(dataMap);
    }
}
