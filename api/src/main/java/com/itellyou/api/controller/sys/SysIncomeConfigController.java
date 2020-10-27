package com.itellyou.api.controller.sys;


import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysIncomeConfigModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.sys.SysIncomeConfigService;
import com.itellyou.service.sys.SysIncomeConfigSingleService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.Params;
import com.itellyou.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/system/income/config")
public class SysIncomeConfigController {

    private final SysIncomeConfigSingleService singleService;
    private final SysIncomeConfigService configService;

    public SysIncomeConfigController(SysIncomeConfigSingleService singleService, SysIncomeConfigService configService) {
        this.singleService = singleService;
        this.configService = configService;
    }

    @GetMapping("/list")
    public ResultModel list(@RequestParam Map args){
        Params params = new Params(args);
        Integer offset = params.getPageOffset(0);
        Integer limit = params.getPageLimit(20);
        Long beginDate = params.getTimestamp("begin");
        Long endDate = params.getTimestamp("end");
        Long ipLong = params.getIPLong();
        Boolean isDeleted = params.getBoolean("deleted",false);
        Map<String,String> orderMap = params.getOrderDefault("created_time","desc","created_time");
        PageModel<SysIncomeConfigModel> pageModel = singleService.page(null,params.get("name"),isDeleted,null,beginDate,endDate,ipLong,orderMap,offset,limit);
        return new ResultModel(pageModel);
    }

    @PostMapping("/add")
    public ResultModel add(HttpServletRequest request, UserInfoModel userModel , @RequestBody Map args){
        Params params = new Params(args);
        String name = params.get("name");
        String remark = params.get("remark");
        if(StringUtils.isEmpty(name)) return new ResultModel(0,"请输入配置名称");
        Double scale = params.getDouble("scale",0.00);
        if(scale < 0 || scale > 100) return new ResultModel(0,"分成比例在0-100之间");
        SysIncomeConfigModel configModel = new SysIncomeConfigModel();
        configModel.setName(name);
        configModel.setScale(scale);
        configModel.setRemark(remark);
        configModel.setCreatedUserId(userModel.getId());
        configModel.setCreatedTime(DateUtils.toLocalDateTime());
        configModel.setCreatedIp(IPUtils.toLong(request));
        return configService.insert(configModel) > 0 ? new ResultModel() : new ResultModel(0,"增加失败");
    }

    @PutMapping("/update")
    public ResultModel update(HttpServletRequest request, UserInfoModel userModel , @RequestBody Map args){
        Params params = new Params(args);
        Long id = params.getLong("id",0l);
        String name = params.get("name");
        String remark = params.get("remark");
        if(StringUtils.isEmpty(name)) return new ResultModel(0,"请输入配置名称");
        Double scale = params.getDouble("scale",0.00);
        if(scale < 0 || scale > 100) return new ResultModel(0,"分成比例在0-100之间");
        SysIncomeConfigModel configModel = new SysIncomeConfigModel();
        configModel.setId(id);
        configModel.setName(name);
        configModel.setScale(scale);
        configModel.setRemark(remark);
        configModel.setUpdatedUserId(userModel.getId());
        configModel.setUpdatedTime(DateUtils.toLocalDateTime());
        configModel.setUpdatedIp(IPUtils.toLong(request));
        int result = configService.updateById(configModel);
        return result == 1 ? new ResultModel() : new ResultModel(0,"更新失败");
    }

    @DeleteMapping("/delete")
    public ResultModel delete(HttpServletRequest request, UserInfoModel userModel ,@RequestParam Map args){
        Params params = new Params(args);
        Long id = params.getLong("id");
        if(id == null) return new ResultModel(0,"id不能为空");
        SysIncomeConfigModel configModel = new SysIncomeConfigModel();
        configModel.setId(id);
        configModel.setScale(null);
        configModel.setIsDeleted(true);
        configModel.setUpdatedUserId(userModel.getId());
        configModel.setUpdatedTime(DateUtils.toLocalDateTime());
        configModel.setUpdatedIp(IPUtils.toLong(request));
        int result = configService.updateById(configModel);
        return result == 1 ? new ResultModel() : new ResultModel(0,"删除失败");
    }
}
