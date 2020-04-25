package com.itellyou.api.controller;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.reward.RewardConfigModel;
import com.itellyou.model.reward.RewardType;
import com.itellyou.service.reward.RewardConfigService;
import com.itellyou.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/reward")
public class RewardController {
    private final RewardConfigService rewardService;

    public RewardController(RewardConfigService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/config")
    public Result config(@RequestParam(required = false) String id){
        if(!StringUtils.isNotEmpty(id)) id = "default";
        Map<RewardType, RewardConfigModel> config = rewardService.findById(id);
        return new Result(config);
    }
}
