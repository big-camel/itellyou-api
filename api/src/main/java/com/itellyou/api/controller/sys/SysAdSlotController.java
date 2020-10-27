package com.itellyou.api.controller.sys;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysAdSlotModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.sys.SysAdSlotService;
import com.itellyou.service.sys.SysAdSlotSingleService;
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
@RequestMapping("/system/ad/slot")
public class SysAdSlotController {

    private final SysAdSlotSingleService adSlotSingleService;
    private final SysAdSlotService adSlotService;

    public SysAdSlotController(SysAdSlotSingleService adSlotSingleService, SysAdSlotService adSlotService) {
        this.adSlotSingleService = adSlotSingleService;
        this.adSlotService = adSlotService;
    }

    @GetMapping("/list")
    public ResultModel list(@RequestParam Map args){
        try {
            Params params = new Params(args);
            String name = params.get("name");
            Long adId = params.getLong("ad_id");
            Integer offset = params.getPageOffset(0);
            Integer limit = params.getPageLimit(20);
            Long beginTime = params.getTimestamp("begin");
            Long endTime = params.getTimestamp("end");

            Map<String,String> orderMap = params.getOrderDefault("created_time","desc","created_time");
            PageModel<SysAdSlotModel> list = adSlotSingleService.page(null, name,adId,null,beginTime,endTime,null,orderMap,offset,limit);
            return new ResultModel(list);
        }catch (Exception e){
            return new ResultModel(500,e.getLocalizedMessage());
        }
    }

    @PostMapping("/add")
    public ResultModel add(HttpServletRequest request, UserInfoModel userModel , @RequestBody Map args){
        Params params = new Params(args);
        String name = params.get("name");
        if(StringUtils.isEmpty(name)) return new ResultModel(500,"请输入名称");
        Long adId = params.getLong("ad_id");
        if(adId == null) return new ResultModel(500,"请选择广告ID");
        String slotId = params.get("slot_id");
        Integer width = params.getInteger("width");
        Integer height = params.getInteger("height");
        String style = params.get("style");
        String format = params.get("format");

        SysAdSlotModel adSlotModel = new SysAdSlotModel();
        adSlotModel.setName(name);
        adSlotModel.setAdId(adId);
        adSlotModel.setSlotId(slotId);
        adSlotModel.setWidth(width);
        adSlotModel.setHeight(height);
        adSlotModel.setStyle(style);
        adSlotModel.setFormat(format);
        adSlotModel.setCreatedUserId(userModel.getId());
        adSlotModel.setCreatedTime(DateUtils.toLocalDateTime());
        adSlotModel.setCreatedIp(IPUtils.toLong(request));

        int result = adSlotService.insert(adSlotModel);
        if(result != 1) return new ResultModel(500,"新增失败");
        return new ResultModel();
    }

    @PutMapping("/update")
    public ResultModel update(HttpServletRequest request, UserInfoModel userModel , @RequestBody Map args){
        Params params = new Params(args);
        Long id = params.getLong("id");
        if(id == null) return new ResultModel(500,"请输入编号");
        String name = params.get("name");
        String slotId = params.get("slot_id");
        Integer width = params.getInteger("width");
        Integer height = params.getInteger("height");
        String style = params.get("style");
        String format = params.get("format");

        SysAdSlotModel adSlotModel = new SysAdSlotModel();
        adSlotModel.setId(id);
        adSlotModel.setName(name);
        adSlotModel.setSlotId(slotId);
        adSlotModel.setWidth(width);
        adSlotModel.setHeight(height);
        adSlotModel.setStyle(style);
        adSlotModel.setFormat(format);
        adSlotModel.setUpdatedUserId(userModel.getId());
        adSlotModel.setUpdatedTime(DateUtils.toLocalDateTime());
        adSlotModel.setUpdatedIp(IPUtils.toLong(request));

        int result = adSlotService.updateById(adSlotModel);
        if(result != 1) return new ResultModel(500,"更新失败");
        return new ResultModel();
    }

    @DeleteMapping("/delete")
    public ResultModel delete(@RequestParam Map args){
        Params params = new Params(args);
        Long id = params.getLong("id");
        if(id == null) return new ResultModel(500,"请输入编号");
        int result = adSlotService.deleteById(id);
        if(result != 1) return new ResultModel(500,"删除失败");
        return new ResultModel();
    }
}
