package com.itellyou.api.controller.user;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserRankModel;
import com.itellyou.service.user.rank.UserRankSearchService;
import com.itellyou.service.user.rank.UserRankService;
import com.itellyou.service.user.rank.UserRankSingleService;
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
    private final UserRankSearchService rankSearchService;
    private final UserRankSingleService rankSingleService;

    public RankController(UserRankService rankService, UserRankSearchService rankSearchService, UserRankSingleService rankSingleService) {
        this.rankService = rankService;
        this.rankSearchService = rankSearchService;
        this.rankSingleService = rankSingleService;
    }

    @GetMapping("")
    public ResultModel list(@RequestParam Map args){
        Params params = new Params(args);
        Integer offset = params.getOrDefault("offset",Integer.class,0);
        Integer limit = params.getOrDefault("limit",Integer.class,20);
        Integer minScore = params.getOrDefault("min_score",Integer.class,null);
        Integer maxScore = params.getOrDefault("max_score",Integer.class,null);
        String name = params.get("name");
        String beginTime = params.get("begin");
        Long begin = DateUtils.getTimestamp(beginTime);
        String endTime = params.get("end");
        Long end = DateUtils.getTimestamp(endTime);
        String ip = params.get("ip");
        Long ipLong = IPUtils.toLong(ip,null);

        Map<String,String> order = new HashMap<>();
        order.put("min_score","asc");
        return new ResultModel(rankSearchService.page(null,name,minScore,maxScore,null,begin,end,ipLong,order,offset,limit),new Labels.LabelModel(UserRankModel.class,"*"));
    }

    @GetMapping("/query/name")
    public ResultModel queryName(@RequestParam @NotBlank String name){
        UserRankModel model = rankSingleService.findByName(name);
        if(model != null){
            return new ResultModel(500,"名称不可用",name);
        }
        return new ResultModel(name);
    }

    @PutMapping("")
    public ResultModel add(HttpServletRequest request, UserInfoModel userModel, @MultiRequestBody @NotBlank String name ,
                           @MultiRequestBody("min_score") @NotNull Integer minScore,
                           @MultiRequestBody("max_score") @NotNull Integer maxScore){

        UserRankModel model = rankSingleService.findByName(name);
        if(model != null){
            return new ResultModel(500,"名称不可用",name);
        }
        UserRankModel rankModel = new UserRankModel();
        rankModel.setName(name);
        rankModel.setMinScore(minScore);
        rankModel.setMaxScore(maxScore);
        rankModel.setCreatedUserId(userModel.getId());
        rankModel.setCreatedTime(DateUtils.toLocalDateTime());
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
            UserRankModel model = rankSingleService.findByName(name);
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
