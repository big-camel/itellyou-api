package com.itellyou.api.controller.admin;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.statistics.StatisticsIncomeModel;
import com.itellyou.model.statistics.StatisticsInfoModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.service.statistics.StatisticsIncomeSingleService;
import com.itellyou.service.statistics.StatisticsSingleService;
import com.itellyou.util.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/admin/home")
public class AdminHomeController {

    private final StatisticsSingleService singleService;
    private final StatisticsIncomeSingleService incomeSingleService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AdminHomeController(StatisticsSingleService singleService, StatisticsIncomeSingleService incomeSingleService) {
        this.singleService = singleService;
        this.incomeSingleService = incomeSingleService;
    }

    @GetMapping("/statistics")
    public ResultModel statistics(@RequestParam Map args){
        try {
            Params params = new Params(args);
            EntityType entityType = params.get("data_type",EntityType.class);
            Integer offset = params.getPageOffset(2);
            Integer limit = params.getPageLimit(20);
            Long beginDate = params.getTimestamp("begin");
            Long endDate = params.getTimestamp("end");

            Map<String,String> orderMap = params.getOrderDefault("date","desc","date","view_count","comment_count","support_count","star_count");
            PageModel<StatisticsInfoModel> list = singleService.pageGroupByDate(null, entityType, null, beginDate,endDate,null,null,orderMap,offset,limit);
            return new ResultModel(list);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            return new ResultModel(500,e.getLocalizedMessage());
        }
    }

    @GetMapping("/income")
    public ResultModel income(@RequestParam Map args){
        try {
            Params params = new Params(args);
            Integer offset = params.getPageOffset(0);
            Integer limit = params.getPageLimit(20);
            Long beginDate = params.getTimestamp("begin");
            Long endDate = params.getTimestamp("end");

            Map<String,String> orderMap = params.getOrderDefault("date","desc","date","total_amount","tip_amount","reward_amount","sharing_amount","sell_amount");
            PageModel<StatisticsIncomeModel> list = incomeSingleService.pageGroupByDate(null, beginDate,endDate,null,null,orderMap,offset,limit);
            return new ResultModel(list);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            return new ResultModel(500,e.getLocalizedMessage());
        }
    }
}
