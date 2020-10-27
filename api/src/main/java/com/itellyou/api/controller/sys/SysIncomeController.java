package com.itellyou.api.controller.sys;

import com.alibaba.fastjson.JSONArray;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.statistics.StatisticsIncomeQueueModel;
import com.itellyou.model.statistics.StatisticsIncomeStepModel;
import com.itellyou.model.statistics.StatisticsInfoModel;
import com.itellyou.model.sys.*;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.statistics.StatisticsIncomeManageService;
import com.itellyou.service.statistics.StatisticsIncomeQueueService;
import com.itellyou.service.statistics.StatisticsSingleService;
import com.itellyou.service.sys.SysIncomeRelatedSearchService;
import com.itellyou.service.sys.SysIncomeService;
import com.itellyou.service.sys.SysIncomeSingleService;
import com.itellyou.service.sys.SysIncomeTipConfigSingleService;
import com.itellyou.util.ArithmeticUtils;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.Params;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/system/income")
public class SysIncomeController {

    private final SysIncomeSingleService singleService;
    private final SysIncomeService incomeService;
    private final SysIncomeRelatedSearchService relatedSearchService;
    private final StatisticsSingleService statisticsSingleService;
    private final SysIncomeTipConfigSingleService configSingleService;
    private final StatisticsIncomeQueueService incomeQueueService;
    private final StatisticsIncomeManageService incomeManageService;

    public SysIncomeController(SysIncomeSingleService singleService, SysIncomeService incomeService, SysIncomeRelatedSearchService relatedSearchService, StatisticsSingleService statisticsSingleService, SysIncomeTipConfigSingleService configSingleService, StatisticsIncomeQueueService incomeQueueService, StatisticsIncomeManageService incomeManageService) {
        this.singleService = singleService;
        this.incomeService = incomeService;
        this.relatedSearchService = relatedSearchService;
        this.statisticsSingleService = statisticsSingleService;
        this.configSingleService = configSingleService;
        this.incomeQueueService = incomeQueueService;
        this.incomeManageService = incomeManageService;
    }

    @GetMapping("/list")
    public ResultModel list(@RequestParam Map args){
        Params params = new Params(args);
        Integer offset = params.getPageOffset(0);
        Integer limit = params.getPageLimit(20);
        Long begin = params.getTimestamp("begin");
        Long end = params.getTimestamp("end");
        Long ipLong = params.getIPLong();
        Map<String,String> orderMap = params.getOrderDefault("created_time","desc","created_time");
        PageModel<SysIncomeModel> pageModel = singleService.page(null,null,null,null,begin,end,ipLong,orderMap,offset,limit);
        return new ResultModel(pageModel);
    }

    @PostMapping("/add")
    public ResultModel add(HttpServletRequest request, UserInfoModel infoModel, @MultiRequestBody @NotNull String date, @MultiRequestBody @NotNull Map<String,Object> value){
        Map<Long,Double> valueMap = new LinkedHashMap<>();
        value.forEach((k,v) -> {
            valueMap.put(Long.parseLong(k),Double.parseDouble(v.toString()));
        });
        Long dateLong = DateUtils.getTimestamp(date);
        if(incomeService.add(DateUtils.toLocalDate(dateLong),valueMap,infoModel.getId(), IPUtils.toLong(request)))
            return new ResultModel();
        return new ResultModel(500,"添加失败");
    }

    @GetMapping("/related")
    public ResultModel related(@RequestParam(value = "income_id") Long incomeId){
        List<SysIncomeRelatedDetailModel> detailModels = relatedSearchService.search(null,incomeId,null,null,null,null,null,null,null,null);
        return new ResultModel(detailModels);
    }

    @PostMapping("/distribution")
    public ResultModel distribution(@RequestBody Map<String,Object> args){
        Params params = new Params(args);
        Long incomeId = params.getLong("income_id");
        if(incomeId == null) return new ResultModel(500,"利润编号不能为空");
        SysIncomeModel incomeModel = singleService.find(incomeId);
        if(incomeModel == null) return new ResultModel(500,"利润编号错误");
        JSONArray relatedJSONArray = params.getOrDefault("related_ids", JSONArray.class,new JSONArray());
        if(relatedJSONArray.size() == 0) return new ResultModel(500,"配置ID不能为空");
        Collection<Long> relatedIds = relatedJSONArray.toJavaList(Long.class);

        JSONArray tipJSONArray = params.get("tip_ids",JSONArray.class,new JSONArray());
        if(tipJSONArray.size() == 0) return new ResultModel(500,"规则ID不能为空");
        Collection<Long> tipIds = tipJSONArray.toJavaList(Long.class);

        List<SysIncomeRelatedDetailModel> relatedDetailModels = relatedSearchService.search(relatedIds,incomeId,null,null,null,null,null,null,null,null);
        if(relatedDetailModels.size() == 0) return new ResultModel(500,"配置ID有误");
        List<SysIncomeTipConfigModel> configModels = configSingleService.search(tipIds,null,null,null,null,null,null,null,null,null);
        if(configModels.size() == 0) return new ResultModel(500,"规则ID有误");
        Map<EntityType,SysIncomeTipConfigModel> configModelMap = configModels.stream().collect(Collectors.toMap(SysIncomeTipConfigModel::getDataType,model -> model));
        Long date = DateUtils.getTimestamp(incomeModel.getDate());
        List<StatisticsInfoModel> statisticsInfoModels = statisticsSingleService.searchGroupByUserAndType(null,null,null,date,date,null,null,null,null,null);
        //需要分配的总金额
        Double totalAmount = 0.0;
        for (SysIncomeRelatedDetailModel relatedModel : relatedDetailModels) {
            Double amount = relatedModel.getAmount();
            SysIncomeConfigModel configModel = relatedModel.getConfig();
            amount = ArithmeticUtils.multiply(amount, configModel.getScale());
            totalAmount = ArithmeticUtils.add(totalAmount, amount);
        }
        AtomicReference<Double> totalScore = new AtomicReference<>(0.00);
        Map<Long,Map<EntityType,Double>> userScore = new HashMap<>();
        //计算分数
        statisticsInfoModels.stream().forEach(statisticsInfoModel -> {
            Long userId = statisticsInfoModel.getUserId();
            EntityType entityType = statisticsInfoModel.getDataType();
            SysIncomeTipConfigModel configModel = configModelMap.get(entityType);
            //如果不符合条件过滤
            if(statisticsInfoModel.getViewCount() < configModel.getMinView()) return;
            if(statisticsInfoModel.getCommentCount() < configModel.getMinComment()) return;
            if(statisticsInfoModel.getSupportCount() < configModel.getMinSupport()) return;
            if(statisticsInfoModel.getOpposeCount() < configModel.getMinOppose()) return;
            if(statisticsInfoModel.getStarCount() < configModel.getMinStar()) return;
            //计算权重分
            Double viewScore = ArithmeticUtils.multiply(statisticsInfoModel.getViewCount(),configModel.getViewWeight());
            Double commentScore = ArithmeticUtils.multiply(statisticsInfoModel.getCommentCount(),configModel.getCommentWeight());
            Double supportScore = ArithmeticUtils.multiply(statisticsInfoModel.getSupportCount(),configModel.getSupportWeight());
            Double opposeScore = ArithmeticUtils.multiply(statisticsInfoModel.getOpposeCount(),configModel.getOpposeWeight());
            Double starScore = ArithmeticUtils.multiply(statisticsInfoModel.getStarCount(),configModel.getStarWeight());
            //总得分
            Double userTotalScore = ArithmeticUtils.add(viewScore,commentScore,supportScore,opposeScore,starScore);
            userScore.computeIfAbsent(userId,key -> new HashMap<>()).put(entityType,userTotalScore);

            totalScore.set(ArithmeticUtils.add(totalScore.get(), userTotalScore));
        });
        //按照分数排序，并且判断是否超过最大派送用户数
        Map<Long,Map<EntityType,Double>> userSortScore = new HashMap<>();
        configModelMap.forEach((type,configModel) -> {
            Map<Long,Double> typeScoreMap = new HashMap<>();
            userScore.forEach((userId,scoreMap) -> {
                scoreMap.forEach((entityType,score) -> {
                    if(typeScoreMap.containsKey(entityType)) typeScoreMap.put(userId,ArithmeticUtils.add(score,typeScoreMap.get(userId)));
                    else typeScoreMap.put(userId,score);
                });
            });
            List<Map.Entry<Long,Double>> list = new ArrayList<>(typeScoreMap.entrySet());
            //降序排序
            Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
            for(Map.Entry<Long,Double> entry:list){
                int index = list.indexOf(entry);
                if(index + 1 > configModel.getMaxUserCount()){
                    break;
                }
                Map<EntityType,Double> sortScore = userSortScore.computeIfAbsent(entry.getKey(),key -> new HashMap<>());
                if(sortScore.containsKey(type)) sortScore.put(type,ArithmeticUtils.add(entry.getValue(),sortScore.get(entry.getKey())));
                else sortScore.put(type,entry.getValue());
            }
        });
        if(totalScore.get() <= 0.00 || totalAmount <= 0.00) return new ResultModel(500,"无可分配的对象");
        //计算出需要派送的用户金额
        Map<Long,Double> userTotalAmount = new HashMap<>();
        Double avgAmount = ArithmeticUtils.divide(totalAmount,totalScore.get());
        userSortScore.forEach((userId,scoreMap) -> {
            scoreMap.forEach((entityType,score) -> {
                Double amount = ArithmeticUtils.multiply(avgAmount,score);
                SysIncomeTipConfigModel configModel = configModelMap.get(entityType);
                if(amount < configModel.getMinAmount()) return;
                if(amount > configModel.getMaxAmount()) amount = configModel.getMaxAmount();

                if(userTotalAmount.containsKey(userId)){
                    userTotalAmount.put(userId,ArithmeticUtils.add(userTotalAmount.get(userId),amount));
                }else{
                    userTotalAmount.put(userId,amount);
                }
            });
        });
        //加入收益统计队列
        userTotalAmount.forEach((userId,amount) -> {
            StatisticsIncomeQueueModel queueModel = new StatisticsIncomeQueueModel();
            StatisticsIncomeStepModel incomeStepModel = new StatisticsIncomeStepModel();
            incomeStepModel.setUserId(userId);
            incomeStepModel.setSharingStep(amount);
            queueModel.setUserId(userId);
            queueModel.setDate(date);
            queueModel.setData(incomeStepModel);
            incomeQueueService.put(queueModel);
        });
        //提交队列
        incomeManageService.run();
        return new ResultModel();
    }
}
