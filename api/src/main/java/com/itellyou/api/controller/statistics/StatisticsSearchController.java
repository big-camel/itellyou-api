package com.itellyou.api.controller.statistics;

import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.article.ArticleTotalModel;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerTotalModel;
import com.itellyou.model.statistics.StatisticsInfoModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.article.ArticleSingleService;
import com.itellyou.service.question.QuestionAnswerSingleService;
import com.itellyou.service.statistics.StatisticsSingleService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

@Validated
@RestController
@RequestMapping("/statistics/search")
public class StatisticsSearchController {

    private final StatisticsSingleService singleService;
    private final ArticleSingleService articleSingleService;
    private final QuestionAnswerSingleService answerSingleService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public StatisticsSearchController(StatisticsSingleService singleService, ArticleSingleService articleSingleService, QuestionAnswerSingleService answerSingleService) {
        this.singleService = singleService;
        this.articleSingleService = articleSingleService;
        this.answerSingleService = answerSingleService;
    }

    @GetMapping("/{type:article|answer}/group")
    public ResultModel groupDateSearch(UserInfoModel userModel, @RequestParam Map args, @PathVariable String type){
        try {
            Params params = new Params(args);
            EntityType entityType = EntityType.valueOf(type.toUpperCase());
            if (entityType == null) return new ResultModel(0, "类型错误");
            Integer offset = params.getPageOffset(null);
            Integer limit = params.getPageLimit(null);
            //当前日期时间戳
            Long nowDate = DateUtils.getTimestamp(DateUtils.toLocalDate());
            //昨日时间戳
            Long yesterday = nowDate - 86400;
            //默认查询最近一周内的数据
            Long beginDate = params.getTimestamp("begin",yesterday - 7 * 86400);
            Long endDate = params.getTimestamp("end",yesterday);
            //最晚日期为昨日
            if(endDate == null || endDate > yesterday) endDate = yesterday;
            //最长90天内的数据
            if(beginDate < endDate - 90 * 86400) beginDate = endDate - 90 * 86400;
            Long id = params.getLong("id");
            Map<String,String> orderMap = params.getOrderDefault("date","desc","date","view_count","comment_count","support_count","star_count");
            List<StatisticsInfoModel> list = singleService.searchGroupByDate(userModel.getId(), entityType, id != null ? new HashSet<Long>(){{add(id);}} : null, beginDate,endDate,null,null,orderMap,offset,limit);
            return new ResultModel(list);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            return new ResultModel(500,e.getLocalizedMessage());
        }
    }

    @GetMapping("/content/{type:article|answer}")
    public ResultModel contentSearch(UserInfoModel userModel, @RequestParam Map args, @PathVariable String type){
        Params params = new Params(args);
        EntityType entityType = EntityType.valueOf(type.toUpperCase());
        Integer offset = params.getPageOffset(0);
        Integer limit = params.getPageLimit(10);
        //当前日期时间戳
        LocalDate localDate = DateUtils.toLocalDate();
        Long nowDate = DateUtils.getTimestamp(localDate);
        //昨日23:59:59时间戳
        Long yesterday = nowDate - 1;
        Long beginDate = params.getTimestamp("begin");
        Long endDate = params.getTimestamp("end");
        //结束日期为日期当前23:59:59的时间戳
        if(endDate != null) endDate = DateUtils.getTimestamp(DateUtils.format(endDate,"yyyy-MM-dd 23:59:59"));
        //最晚日期为昨日
        if(endDate == null || endDate > yesterday) endDate = yesterday;
        Map<String,String> orderMap = params.getOrderDefault("created_time","desc","created_time","view_count","comment_count","support_count","star_count");
        if(entityType.equals(EntityType.ARTICLE)){
            PageModel<ArticleInfoModel> pageModel = articleSingleService.page(null,"version",null,userModel.getId(),null,false,true,false,null,null,null,null,null,null,null,null,null,null,beginDate,endDate,null,orderMap,offset,limit);
            return new ResultModel(pageModel);
        }
        if(entityType.equals(EntityType.ANSWER)){
            PageModel<QuestionAnswerModel> pageModel = answerSingleService.page(null,null,"version",userModel.getId(),null,false,true,false,null,null,null,null,null,null,null,null,null,null,null,beginDate,endDate,orderMap,offset,limit);
            return new ResultModel(pageModel);
        }
        return new ResultModel(404,"Not found");
    }

    @GetMapping("/{type:article|answer}/total")
    public ResultModel total(UserInfoModel userModel, @PathVariable @NotNull String type){
        EntityType entityType = EntityType.valueOf(type.toUpperCase());
        Object total;
        if(entityType.equals(EntityType.ARTICLE)){
            List<ArticleTotalModel> totalModels = articleSingleService.totalByUser(new HashSet<Long>(){{add(userModel.getId());}},false,true,false,null,null,null,null,null);
            Optional<ArticleTotalModel> totalModelOptional = totalModels.stream().filter(totalModel -> totalModel.getUserId().equals(userModel.getId())).findFirst();
            total = totalModelOptional.isPresent() ? totalModelOptional.get() : null;
        }else{
            List<QuestionAnswerTotalModel> totalModels = answerSingleService.totalByUser(new HashSet<Long>(){{add(userModel.getId());}},null,null,false,true,false,null,null,null,null,null);
            Optional<QuestionAnswerTotalModel> totalModelOptional = totalModels.stream().filter(totalModel -> totalModel.getUserId().equals(userModel.getId())).findFirst();
            total = totalModelOptional.isPresent() ? totalModelOptional.get() : null;
        }

        //当前日期时间戳
        LocalDate localDate = DateUtils.toLocalDate();
        Long nowDate = DateUtils.getTimestamp(localDate);
        //昨日时间戳
        Long yesterday = nowDate - 86400;
        //默认查询最近2天的数据
        Long beginDate = yesterday - 86400;
        Long endDate = yesterday;
        Map<String,String> orderMap = new HashMap<String, String>(){{
            put("date","desc");
        }};
        Map<String,Object> data = new HashMap<>();
        data.put("total",total);
        List<StatisticsInfoModel> list = singleService.searchGroupByDate(userModel.getId(), entityType, null, beginDate,endDate,null,null,orderMap,null,null);
        Optional<StatisticsInfoModel> yesterdayModel = list.stream().filter(infoModel -> DateUtils.getTimestamp(infoModel.getDate()).equals(yesterday)).findFirst();
        data.put("yesterday",yesterdayModel.isPresent() ? yesterdayModel.get() : null);
        Optional<StatisticsInfoModel> beforeYesterdayModel = list.stream().filter(infoModel -> DateUtils.getTimestamp(infoModel.getDate()).equals(beginDate)).findFirst();
        data.put("before_yesterday",beforeYesterdayModel.isPresent() ? beforeYesterdayModel.get() : null);
        return new ResultModel(data);
    }
}
