package com.itellyou.api.controller;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.sys.*;
import com.itellyou.model.user.UserBankType;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.sys.RewardConfigService;
import com.itellyou.service.sys.RewardLogService;
import com.itellyou.service.sys.RewardService;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reward")
public class RewardController {
    private final RewardConfigService rewardConfigService;
    private final RewardService rewardService;
    private final RewardLogService rewardLogService;
    private final QuestionAnswerSearchService answerSearchService;

    public RewardController(RewardConfigService rewardConfigService, RewardService rewardService, RewardLogService rewardLogService, QuestionAnswerSearchService answerSearchService) {
        this.rewardConfigService = rewardConfigService;
        this.rewardService = rewardService;
        this.rewardLogService = rewardLogService;
        this.answerSearchService = answerSearchService;
    }

    @GetMapping("/config")
    public ResultModel config(@RequestParam(required = false) String id){
        if(!StringUtils.isNotEmpty(id)) id = "default";
        Map<RewardType, RewardConfigModel> config = rewardConfigService.findById(id);
        return new ResultModel(config);
    }

    @PostMapping("/do")
    public ResultModel doReward(HttpServletRequest request, UserInfoModel userModel, @MultiRequestBody @NotBlank String type,
                                @MultiRequestBody @NotNull Double amount, @MultiRequestBody(value = "data_type") @NotBlank String dataType, @MultiRequestBody(value = "data_key") @NotNull Long dataKey){
        UserBankType bankType;
        EntityType entityType;
        try{
            bankType = UserBankType.valueOf(type.toUpperCase());
            if(!bankType.equals(UserBankType.CASH) && !bankType.equals(UserBankType.CREDIT)) throw new Exception("参数错误");
            entityType = EntityType.valueOf(dataType.toUpperCase());
            if(!entityType.equals(EntityType.ANSWER) && !entityType.equals(EntityType.ARTICLE)) throw new Exception("参数错误");
            RewardLogModel logModel = rewardService.doReward(bankType,amount,entityType,dataKey,userModel.getId(), IPUtils.toLong(request));

            Long searchUserId = userModel != null ? userModel.getId() : null;
            return new ResultModel(rewardLogService.find(logModel.getId(),searchUserId));
        }catch (Exception e){
            return new ResultModel(500,e.getLocalizedMessage());
        }
    }

    @GetMapping("/list")
    public ResultModel list(UserInfoModel userModel, @RequestParam(required = false) String type,
                            @RequestParam(value = "data_type",required = false) String dataType,
                            @RequestParam(value = "data_key",required = false) Long dataKey, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        Long searchUserId = userModel != null ? userModel.getId() : null;

        UserBankType bankType = null;
        EntityType entityType = null;
        try{
            bankType = StringUtils.isNotEmpty(type) ? UserBankType.valueOf(type.toUpperCase()) : null;
            entityType = StringUtils.isNotEmpty(dataType) ? EntityType.valueOf(dataType.toUpperCase()) : null;
        }catch (Exception e){}
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<RewardLogDetailModel> logModels = rewardLogService.page(null,bankType,entityType,new HashSet<Long>(){{add(dataKey);}},searchUserId,null,null,null,null,null,order,offset,limit);
        return new ResultModel(logModels);
    }

    @GetMapping("/answer/list")
    public ResultModel answer(UserInfoModel userModel, @RequestParam(required = false) String type,
                            @RequestParam(value = "question_id",required = false) Long questionId, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        Long searchUserId = userModel != null ? userModel.getId() : null;

        UserBankType bankType = null;
        HashSet<Long> answerIds = new HashSet<>();
        try{

            List<QuestionAnswerDetailModel> answerDetailModels = answerSearchService.search(questionId,null,false,null,false,true,false,null,null,null,null);
            for (QuestionAnswerDetailModel detailModel : answerDetailModels){
                answerIds.add(detailModel.getId());
            }
            bankType = StringUtils.isNotEmpty(type) ? UserBankType.valueOf(type.toUpperCase()) : null;
        }catch (Exception e){}
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<RewardLogDetailModel> logModels = rewardLogService.page(null,bankType,EntityType.ANSWER,answerIds,searchUserId,null,null,null,null,null,order,offset,limit);
        return new ResultModel(logModels);
    }
}
