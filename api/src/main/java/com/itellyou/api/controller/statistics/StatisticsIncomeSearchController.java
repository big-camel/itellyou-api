package com.itellyou.api.controller.statistics;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.statistics.StatisticsIncomeModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.statistics.StatisticsIncomeSingleService;
import com.itellyou.util.ArithmeticUtils;
import com.itellyou.util.DateUtils;
import com.itellyou.util.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/statistics/income/search")
public class StatisticsIncomeSearchController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StatisticsIncomeSingleService incomeSingleService;

    public StatisticsIncomeSearchController(StatisticsIncomeSingleService incomeSingleService) {
        this.incomeSingleService = incomeSingleService;
    }

    @GetMapping("/list")
    public ResultModel list(UserInfoModel userModel, @RequestParam Map args){
        try {
            Params params = new Params(args);
            Integer offset = params.getPageOffset(null);
            Integer limit = params.getPageLimit(null);
            //当前日期时间戳
            LocalDate localDate = DateUtils.toLocalDate();
            Long nowDate = DateUtils.getTimestamp(localDate);
            //昨日00:00:00时间戳
            Long yesterday = nowDate - 86400;
            //默认查询最近一周内的数据
            Long beginDate = params.getTimestamp("begin",yesterday - 7 * 86400);
            Long endDate = params.getTimestamp("end",yesterday);
            //最晚日期为昨日
            if(endDate == null || endDate > yesterday) endDate = yesterday;
            //最长90天内的数据
            if(beginDate < endDate - 90 * 86400) beginDate = endDate - 90 * 86400;
            Map<String,String> orderMap = params.getOrderDefault("date","desc","date","total_amount","tip_amount","reward_amount","sharing_amount","sell_amount");
            List<StatisticsIncomeModel> list = incomeSingleService.search(userModel.getId(), beginDate,endDate,null,null,orderMap,offset,limit);
            return new ResultModel(list);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            return new ResultModel(500,e.getLocalizedMessage());
        }
    }

    @GetMapping("/total")
    public ResultModel total(UserInfoModel userModel){
        //当前日期时间戳
        LocalDate localDate = DateUtils.toLocalDate();
        Long nowDate = DateUtils.getTimestamp(DateUtils.format(localDate,"yyyy-MM-dd 23:59:59"));
        //昨日时间戳
        Long yesterday = nowDate - 86400;
        //查询3个月以前1号到昨天的数据
        LocalDate threeMonthsAgoDate = DateUtils.toLocalDate().minusMonths(2);
        String beginDateStr = DateUtils.format(threeMonthsAgoDate,"yyyy-MM-01");
        Long beginDate = DateUtils.getTimestamp(beginDateStr);
        //结束日期为昨天
        Long endDate = yesterday;

        String currentMonthDate = DateUtils.format(localDate,"yyyy-MM");
        LocalDate prevMonthLocalDate = localDate.minusMonths(1);
        String prevMonthDate = DateUtils.format(prevMonthLocalDate,"yyyy-MM");
        String beforeMonthDate = DateUtils.format(threeMonthsAgoDate,"yyyy-MM");

        Map<String, StatisticsIncomeModel> data = new HashMap<>();
        List<StatisticsIncomeModel> incomeModels = incomeSingleService.search(userModel.getId(),beginDate,endDate,null,null,null,null,null);
        for (StatisticsIncomeModel incomeModel : incomeModels){
            LocalDate modelDate = incomeModel.getDate();
            if(modelDate == null) continue;
            String monthDate = modelDate.getYear() + "-" + modelDate.getMonthValue();
            StatisticsIncomeModel stepModel;
            if(monthDate.equals(currentMonthDate)){
                stepModel = data.computeIfAbsent("current_month",key -> new StatisticsIncomeModel());
            }else if(monthDate.equals(prevMonthDate)){
                stepModel = data.computeIfAbsent("prev_month",key -> new StatisticsIncomeModel());
            }else if(monthDate.equals(beforeMonthDate)){
                stepModel = data.computeIfAbsent("before_month",key -> new StatisticsIncomeModel());
            }else continue;
            stepModel.setTotalAmount(ArithmeticUtils.add(stepModel.getTotalAmount(),incomeModel.getTotalAmount()));
            stepModel.setTipAmount(ArithmeticUtils.add(stepModel.getTipAmount(),incomeModel.getTipAmount()));
            stepModel.setRewardAmount(ArithmeticUtils.add(stepModel.getRewardAmount(),incomeModel.getRewardAmount()));
            stepModel.setSharingAmount(ArithmeticUtils.add(stepModel.getSharingAmount(),incomeModel.getSharingAmount()));
            stepModel.setSellAmount(ArithmeticUtils.add(stepModel.getSellAmount(),incomeModel.getSellAmount()));
            stepModel.setOtherAmount(ArithmeticUtils.add(stepModel.getOtherAmount(),incomeModel.getOtherAmount()));
        }
        return new ResultModel(data);
    }
}
