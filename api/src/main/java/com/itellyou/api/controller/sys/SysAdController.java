package com.itellyou.api.controller.sys;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.AdType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysAdModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.sys.SysAdService;
import com.itellyou.service.sys.SysAdSingleService;
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
@RequestMapping("/system/ad")
public class SysAdController {

    private final SysAdSingleService adSingleService;
    private final SysAdService adService;

    public SysAdController(SysAdSingleService adSingleService, SysAdService adService) {
        this.adSingleService = adSingleService;
        this.adService = adService;
    }

    @GetMapping("/list")
    public ResultModel list(@RequestParam Map args){
        try {
            Params params = new Params(args);
            String name = params.get("name");
            AdType type = params.get("type", AdType.class);
            Integer offset = params.getPageOffset(0);
            Integer limit = params.getPageLimit(20);
            Long beginTime = params.getTimestamp("begin");
            Long endTime = params.getTimestamp("end");

            Map<String,String> orderMap = params.getOrderDefault("created_time","desc","created_time");
            PageModel<SysAdModel> list = adSingleService.page(null, type, name,null,null,null,beginTime,endTime,null,orderMap,offset,limit);
            return new ResultModel(list);
        }catch (Exception e){
            return new ResultModel(500,e.getLocalizedMessage());
        }
    }

    @PostMapping("/add")
    public ResultModel add(HttpServletRequest request, UserInfoModel userModel , @RequestBody Map args){
        Params params = new Params(args);
        String name = params.get("name");
        if(StringUtils.isEmpty(name)) return new ResultModel(500,"请输入广告名称");
        AdType type = params.get("type", AdType.class);
        if(type == null) return new ResultModel(500,"请选择广告类型");
        Boolean enabledCn = params.getBoolean("enabled_cn",false);
        Boolean enabledForeign = params.getBoolean("enabled_foreign",false);
        String dataId = params.get("data_id");

        SysAdModel adModel = new SysAdModel();
        adModel.setName(name);
        adModel.setType(type);
        adModel.setDataId(dataId);
        adModel.setEnabledCn(enabledCn);
        adModel.setEnabledForeign(enabledForeign);
        adModel.setCreatedUserId(userModel.getId());
        adModel.setCreatedTime(DateUtils.toLocalDateTime());
        adModel.setCreatedIp(IPUtils.toLong(request));

        int result = adService.insert(adModel);
        if(result != 1) return new ResultModel(500,"新增失败");
        return new ResultModel();
    }

    @PutMapping("/update")
    public ResultModel update(HttpServletRequest request, UserInfoModel userModel , @RequestBody Map args){
        Params params = new Params(args);
        Long id = params.getLong("id");
        if(id == null) return new ResultModel(500,"请输入广告编号");
        String name = params.get("name");
        AdType type = params.get("type", AdType.class);
        Boolean enabledCn = params.getBoolean("enabled_cn");
        Boolean enabledForeign = params.getBoolean("enabled_foreign");
        String dataId = params.get("data_id");

        SysAdModel adModel = new SysAdModel();
        adModel.setId(id);
        adModel.setName(name);
        adModel.setType(type);
        adModel.setDataId(dataId);
        adModel.setEnabledCn(enabledCn);
        adModel.setEnabledForeign(enabledForeign);
        adModel.setUpdatedUserId(userModel.getId());
        adModel.setUpdatedTime(DateUtils.toLocalDateTime());
        adModel.setUpdatedIp(IPUtils.toLong(request));

        int result = adService.updateById(adModel);
        if(result != 1) return new ResultModel(500,"更新失败");
        return new ResultModel();
    }

    @DeleteMapping("/delete")
    public ResultModel delete(@RequestParam Map args){
        Params params = new Params(args);
        Long id = params.getLong("id");
        if(id == null) return new ResultModel(500,"请输入广告编号");
        int result = adService.deleteById(id);
        if(result != 1) return new ResultModel(500,"删除失败");
        return new ResultModel();
    }
}
