package com.itellyou.api.controller.user;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserRankModel;
import com.itellyou.service.user.UserRankService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.Params;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/user/rank")
public class RankController {

    private final UserRankService rankService;

    public RankController(UserRankService rankService) {
        this.rankService = rankService;
    }

    @GetMapping("")
    public ResultModel list(@RequestParam Map params){
        Integer offset = Params.getOrDefault(params,"offset",Integer.class,0);
        Integer limit = Params.getOrDefault(params,"limit",Integer.class,0);
        Integer minScore = Params.getOrDefault(params,"min_score",Integer.class,null);
        Integer maxScore = Params.getOrDefault(params,"max_score",Integer.class,null);
        String name = Params.getOrDefault(params,"name",String.class,null);
        String beginTime = Params.getOrDefault(params,"begin",String.class,null);
        Long begin = DateUtils.getTimestamp(beginTime);
        String endTime = Params.getOrDefault(params,"end",String.class,null);
        Long end = DateUtils.getTimestamp(endTime);
        String ip = Params.getOrDefault(params,"ip",String.class,null);
        Long ipLong = IPUtils.toLong(ip,null);

        Map<String,String> order = new HashMap<>();
        order.put("min_score","asc");
        return new ResultModel(rankService.page(null,name,minScore,maxScore,null,begin,end,ipLong,order,offset,limit),new Labels.LabelModel(UserRankModel.class,"*"));
    }

    @GetMapping("/query/name")
    public ResultModel queryName(@RequestParam @NotBlank String name){
        UserRankModel model = rankService.findByName(name);
        if(model != null){
            return new ResultModel(500,"名称不可用",name);
        }
        return new ResultModel(name);
    }

    @PutMapping("")
    public ResultModel add(HttpServletRequest request, UserInfoModel userModel, @MultiRequestBody @NotBlank String name ,
                           @MultiRequestBody("min_score") @NotNull Integer minScore,
                           @MultiRequestBody("max_score") @NotNull Integer maxScore){

        UserRankModel model = rankService.findByName(name);
        if(model != null){
            return new ResultModel(500,"名称不可用",name);
        }
        UserRankModel rankModel = new UserRankModel();
        rankModel.setName(name);
        rankModel.setMinScore(minScore);
        rankModel.setMaxScore(maxScore);
        rankModel.setCreatedUserId(userModel.getId());
        rankModel.setCreatedTime(DateUtils.getTimestamp());
        rankModel.setCreatedIp(IPUtils.toLong(request));
        int result = rankService.insert(rankModel);
        if(result != 1) return new ResultModel(0,"新增失败");
        return new ResultModel(rankModel);
    }

    @DeleteMapping("")
    public ResultModel remove(@RequestParam @NotNull Long id){
        int result = rankService.deleteById(id);
        if(result != 1) return new ResultModel(0,"删除失败");
        return new ResultModel();
    }

    @PostMapping("")
    public ResultModel update(@MultiRequestBody @NotNull Long id, @MultiRequestBody(required = false) String name ,
                              @MultiRequestBody(value = "min_score",required = false) Integer minScore,
                              @MultiRequestBody(value = "max_score",required = false) Integer maxScore){
        if(StringUtils.isEmpty(name) && minScore == null && maxScore == null){
            return new ResultModel(500,"参数错误");
        }
        if(StringUtils.isNotEmpty(name)){
            UserRankModel model = rankService.findByName(name);
            if(model != null){
                return new ResultModel(500,"名称不可用",name);
            }
        }
        UserRankModel rankModel = new UserRankModel();
        rankModel.setId(id);
        rankModel.setName(name);
        rankModel.setMinScore(minScore);
        rankModel.setMaxScore(maxScore);
        int result = rankService.update(rankModel);
        if(result != 1) return new ResultModel(500,"更新失败");
        return new ResultModel();
    }
}
