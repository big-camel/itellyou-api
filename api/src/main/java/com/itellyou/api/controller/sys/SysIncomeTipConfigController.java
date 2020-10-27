package com.itellyou.api.controller.sys;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysIncomeTipConfigModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.sys.SysIncomeTipConfigService;
import com.itellyou.service.sys.SysIncomeTipConfigSingleService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.Params;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/system/income/tip/config")
public class SysIncomeTipConfigController {

    private final SysIncomeTipConfigService configService;
    private final SysIncomeTipConfigSingleService singleService;

    public SysIncomeTipConfigController(SysIncomeTipConfigService configService, SysIncomeTipConfigSingleService singleService) {
        this.configService = configService;
        this.singleService = singleService;
    }

    @GetMapping("/list")
    public ResultModel list(@RequestParam Map args){
        Params params = new Params(args);
        Integer offset = params.getPageOffset(0);
        Integer limit = params.getPageLimit(20);
        Long beginTime = params.getTimestamp("begin");
        Long endTime = params.getTimestamp("end");
        Long ipLong = params.getIPLong();
        EntityType dataType = params.get("type",EntityType.class);
        Map<String,String> orderMap = params.getOrderDefault("created_time","desc","created_time");
        PageModel<SysIncomeTipConfigModel> pageModel = singleService.page(null,params.get("name"),dataType,null,beginTime,endTime,ipLong,orderMap,offset,limit);
        return new ResultModel(pageModel);
    }

    @PostMapping("/add")
    public ResultModel add(HttpServletRequest request, UserInfoModel userModel , @RequestBody Map args){
        Params params = new Params(args);
        String name = params.get("name");
        EntityType dataType = params.get("type", Arrays.asList(EntityType.ARTICLE,EntityType.ANSWER,EntityType.SOFTWARE),EntityType.class).value();
        if(dataType == null) return new ResultModel(500,"类型错误");
        Integer minView = params.getInteger("min_view",0);
        Integer minComment = params.getInteger("min_comment",0);
        Integer minSupport = params.getInteger("min_support",0);
        Integer minOppose = params.getInteger("min_oppose",0);
        Integer minStar = params.getInteger("min_star",0);
        Double viewWeight = params.getDouble("view_weight",0.00);
        Double commentWeight = params.getDouble("comment_weight",0.00);
        Double supportWeight = params.getDouble("support_weight",0.00);
        Double opposeWeight = params.getDouble("oppose_weight",0.00);
        Double starWeight = params.getDouble("star_weight",0.00);
        Double minAmount = params.getDouble("min_amount",0.00);
        Double maxAmount = params.getDouble("max_amount",0.00);
        Integer maxUserCount = params.getInteger("max_user_count",0);

        SysIncomeTipConfigModel configModel = new SysIncomeTipConfigModel();
        configModel.setName(name);
        configModel.setDataType(dataType);
        configModel.setMinView(minView);
        configModel.setMinComment(minComment);
        configModel.setMinSupport(minSupport);
        configModel.setMinOppose(minOppose);
        configModel.setMinStar(minStar);
        configModel.setViewWeight(viewWeight);
        configModel.setCommentWeight(commentWeight);
        configModel.setSupportWeight(supportWeight);
        configModel.setOpposeWeight(opposeWeight);
        configModel.setStarWeight(starWeight);
        configModel.setMinAmount(minAmount);
        configModel.setMaxAmount(maxAmount);
        configModel.setMaxUserCount(maxUserCount);
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
        EntityType dataType = params.get("type", Arrays.asList(EntityType.ARTICLE,EntityType.ANSWER,EntityType.SOFTWARE),EntityType.class).value();
        if(dataType == null) return new ResultModel(500,"类型错误");
        Integer minView = params.getInteger("min_view");
        Integer minComment = params.getInteger("min_comment");
        Integer minSupport = params.getInteger("min_support");
        Integer minOppose = params.getInteger("min_oppose");
        Integer minStar = params.getInteger("min_star");
        Double viewWeight = params.getDouble("view_weight");
        Double commentWeight = params.getDouble("comment_weight");
        Double supportWeight = params.getDouble("support_weight");
        Double opposeWeight = params.getDouble("oppose_weight");
        Double starWeight = params.getDouble("star_weight");
        Double minAmount = params.getDouble("min_amount");
        Double maxAmount = params.getDouble("max_amount");
        Integer maxUserCount = params.getInteger("max_user_count");
        SysIncomeTipConfigModel configModel = new SysIncomeTipConfigModel();
        configModel.setId(id);
        configModel.setName(name);
        configModel.setDataType(dataType);
        configModel.setMinView(minView);
        configModel.setMinComment(minComment);
        configModel.setMinSupport(minSupport);
        configModel.setMinOppose(minOppose);
        configModel.setMinStar(minStar);
        configModel.setViewWeight(viewWeight);
        configModel.setCommentWeight(commentWeight);
        configModel.setSupportWeight(supportWeight);
        configModel.setOpposeWeight(opposeWeight);
        configModel.setStarWeight(starWeight);
        configModel.setMinAmount(minAmount);
        configModel.setMaxAmount(maxAmount);
        configModel.setMaxUserCount(maxUserCount);
        configModel.setUpdatedUserId(userModel.getId());
        configModel.setUpdatedTime(DateUtils.toLocalDateTime());
        configModel.setUpdatedIp(IPUtils.toLong(request));
        int result = configService.updateById(configModel);
        return result == 1 ? new ResultModel() : new ResultModel(0,"更新失败");
    }

    @DeleteMapping("/delete")
    public ResultModel delete(@RequestParam Map args){
        Params params = new Params(args);
        Long id = params.getLong("id");
        if(id == null) return new ResultModel(0,"id不能为空");
        int result = configService.deleteById(id);
        return result == 1 ? new ResultModel() : new ResultModel(0,"删除失败");
    }
}
