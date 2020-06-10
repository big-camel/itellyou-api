package com.itellyou.api.controller.question;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerStarDetailModel;
import com.itellyou.model.question.QuestionAnswerStarModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.common.StarService;
import com.itellyou.service.common.impl.StarFactory;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionAnswerSingleService;
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

    private final StarService<QuestionAnswerStarModel> starService;
    private final QuestionAnswerSearchService searchService;
    private final QuestionAnswerSingleService answerSingleService;

    public AnswerStarController(StarFactory starFactory, QuestionAnswerSearchService searchService, QuestionAnswerSingleService answerSingleService){
        this.starService = starFactory.create(EntityType.ANSWER);
        this.searchService = searchService;
        this.answerSingleService = answerSingleService;
    }

    @GetMapping("/star")
    public ResultModel star(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new ResultModel(401,"未登陆");
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<QuestionAnswerStarDetailModel> pageData = (PageModel<QuestionAnswerStarDetailModel>) starService.page(null,userModel.getId(),null,null,null,order,offset,limit);
        return new ResultModel(pageData,new Labels.LabelModel(QuestionAnswerDetailModel.class,"base","question"));
    }

    @PostMapping("/star")
    public ResultModel star(HttpServletRequest request, UserInfoModel userModel, @MultiRequestBody @NotNull Long id){
        if(userModel == null) return new ResultModel(401,"未登陆");
        QuestionAnswerModel infoModel = answerSingleService.findById(id);
        if(infoModel == null) return new ResultModel(404,"错误的id");
        if(userModel.isDisabled()) return new ResultModel(0,"错误的用户状态");
        String clientIp = IPUtils.getClientIp(request);
        Long ip = IPUtils.toLong(clientIp);
        QuestionAnswerStarModel starModel = new QuestionAnswerStarModel(id, DateUtils.getTimestamp(),userModel.getId(),ip);
        try{
            int result = starService.insert(starModel);
            if(result != 1) throw new Exception("收藏失败");
            return new ResultModel(infoModel.getStarCount() + 1);
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @DeleteMapping("/star")
    public ResultModel delete(UserInfoModel userModel, HttpServletRequest request, @MultiRequestBody @NotNull Long id){
        if(userModel == null) return new ResultModel(401,"未登陆");
        if(userModel.isDisabled()) return new ResultModel(0,"错误的用户状态");
        try{
            String clientIp = IPUtils.getClientIp(request);
            Long ip = IPUtils.toLong(clientIp);
            int result = starService.delete(id,userModel.getId(),ip);
            if(result != 1) throw new Exception("取消收藏失败");
            QuestionAnswerModel infoModel = answerSingleService.findById(id);
            return new ResultModel(infoModel.getStarCount());
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }
}
