package com.itellyou.api.controller.question;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping(value = {"/answer"})
public class AnswerSearchController {

    private final QuestionAnswerSearchService searchService;

    public AnswerSearchController(QuestionAnswerSearchService searchService){
        this.searchService = searchService;
    }

    @GetMapping("/list")
    public Result list(UserInfoModel userModel,@RequestParam(required = false,name = "user_id") Long userId, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){

        Long searchUserId = userModel == null ? null : userModel.getId();
        Map<String,String > order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<QuestionAnswerDetailModel> pageData = searchService.page(null,searchUserId, userId,true,null,false,true,false,null,null,
                order,offset,limit);
        return new Result(pageData,new Labels.LabelModel(QuestionAnswerDetailModel.class,"base","question"));
    }

    @GetMapping("/group-user")
    public Result group(UserInfoModel userModel, @RequestParam(required = false) Integer day, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        Long searchId = userModel == null ? null : userModel.getId();
        if(day == null || day > 30) day = 30;
        Long beginTime = DateUtils.getTimestamp() - day * 86400;
        Map<String,String > order = new HashMap<>();
        order.put("count","desc");
        return new Result(searchService.groupByUserId(null,searchId,null,null,null,null,beginTime,null,order,offset,limit));
    }
}
