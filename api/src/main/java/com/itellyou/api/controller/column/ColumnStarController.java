package com.itellyou.api.controller.column;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.column.ColumnStarDetailModel;
import com.itellyou.model.column.ColumnStarModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.column.ColumnSearchService;
import com.itellyou.service.common.StarService;
import com.itellyou.service.common.impl.StarFactory;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/column")
public class ColumnStarController {

    private final StarService<ColumnStarModel> starService;
    private final ColumnSearchService searchService;

    public ColumnStarController(StarFactory starFactory, ColumnSearchService searchService){
        this.starService = starFactory.create(EntityType.COLUMN);
        this.searchService = searchService;
    }

    @GetMapping("/star")
    public ResultModel star(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new ResultModel(401,"未登陆");
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<ColumnStarDetailModel> pageData = (PageModel<ColumnStarDetailModel>) starService.page(null,userModel.getId(),null,null,null,order,offset,limit);
        return new ResultModel(pageData);
    }

    @PostMapping("/star")
    public ResultModel star(HttpServletRequest request, UserInfoModel userModel, @MultiRequestBody @NotNull Long id){
        if(userModel == null) return new ResultModel(401,"未登陆");
        ColumnInfoModel infoModel = searchService.findById(id);
        if(infoModel == null) return new ResultModel(404,"错误的id");
        if(userModel.isDisabled()) return new ResultModel(0,"错误的用户状态");
        String clientIp = IPUtils.getClientIp(request);
        Long ip = IPUtils.toLong(clientIp);
        ColumnStarModel starModel = new ColumnStarModel(id, DateUtils.getTimestamp(),userModel.getId(),ip);
        try{
            int result = starService.insert(starModel);
            if(result != 1) throw new Exception("关注失败");
            return new ResultModel(infoModel.getStarCount() + 1);
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @DeleteMapping("/star")
    public ResultModel delete(UserInfoModel userModel, HttpServletRequest request, @MultiRequestBody @NotNull Long id){
        if(userModel == null) return new ResultModel(401,"未登陆");
        if(userModel.isDisabled()) return new ResultModel(0,"错误的用户状态");
        try{
            String clientIp = IPUtils.getClientIp(request);
            Long ip = IPUtils.toLong(clientIp);
            int result = starService.delete(id,userModel.getId(),ip);
            if(result != 1) throw new Exception("取消关注失败");
            ColumnInfoModel infoModel = searchService.findById(id);
            return new ResultModel(infoModel.getStarCount());
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }
}
