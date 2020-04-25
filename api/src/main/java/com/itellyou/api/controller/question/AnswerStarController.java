package com.itellyou.api.controller.question;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerStarDetailModel;
import com.itellyou.model.question.QuestionAnswerStarModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionAnswerService;
import com.itellyou.service.question.QuestionAnswerStarService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/answer")
public class AnswerStarController {

    private final QuestionAnswerStarService starService;
    private final QuestionAnswerService answerService;
    private final QuestionAnswerSearchService searchService;

    public AnswerStarController(QuestionAnswerStarService starService, QuestionAnswerService answerService,QuestionAnswerSearchService searchService){
        this.starService = starService;
        this.answerService = answerService;
        this.searchService = searchService;
    }

    @GetMapping("/star")
    public Result star(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new Result(401,"未登陆");
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<QuestionAnswerStarDetailModel> pageData = starService.page(null,userModel.getId(),null,null,null,order,offset,limit);
        return new Result(pageData,new Labels.LabelModel(QuestionAnswerDetailModel.class,"base","question"));
    }

    @PostMapping("/star")
    public Result star(HttpServletRequest request,UserInfoModel userModel, @MultiRequestBody @NotNull Long id){
        if(userModel == null) return new Result(401,"未登陆");
        QuestionAnswerModel infoModel = searchService.findById(id);
        if(infoModel == null) return new Result(404,"错误的id");
        if(userModel.isDisabled()) return new Result(0,"错误的用户状态");
        String clientIp = IPUtils.getClientIp(request);
        Long ip = IPUtils.toLong(clientIp);
        QuestionAnswerStarModel starModel = new QuestionAnswerStarModel(id, DateUtils.getTimestamp(),userModel.getId(),ip);
        try{
            int result = starService.insert(starModel);
            if(result != 1) throw new Exception("收藏失败");
            return new Result(infoModel.getStarCount() + 1);
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }

    @DeleteMapping("/star")
    public Result delete(UserInfoModel userModel, @MultiRequestBody @NotNull Long id){
        if(userModel == null) return new Result(401,"未登陆");
        if(userModel.isDisabled()) return new Result(0,"错误的用户状态");
        try{
            int result = starService.delete(id,userModel.getId());
            if(result != 1) throw new Exception("取消收藏失败");
            QuestionAnswerModel infoModel = searchService.findById(id);
            return new Result(infoModel.getStarCount());
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }
}
