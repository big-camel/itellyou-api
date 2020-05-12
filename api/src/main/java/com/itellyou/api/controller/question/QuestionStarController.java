package com.itellyou.api.controller.question;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.question.QuestionStarDetailModel;
import com.itellyou.model.question.QuestionStarModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.common.StarService;
import com.itellyou.service.common.impl.StarFactory;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/question")
public class QuestionStarController {

    private final StarService<QuestionStarModel> starService;
    private final QuestionSearchService questionSearchService;

    public QuestionStarController(StarFactory starFactory, QuestionSearchService questionSearchService){
        this.starService = starFactory.create(EntityType.QUESTION);
        this.questionSearchService = questionSearchService;
    }

    @GetMapping("/star")
    public ResultModel star(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new ResultModel(401,"未登陆");
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<QuestionStarDetailModel> pageData = (PageModel<QuestionStarDetailModel>) starService.page(null,userModel.getId(),null,null,null,order,offset,limit);
        return new ResultModel(pageData);
    }

    @PostMapping("/star")
    public ResultModel star(HttpServletRequest request, UserInfoModel userModel, @MultiRequestBody @NotNull Long id){
        if(userModel == null) return new ResultModel(401,"未登陆");
        QuestionInfoModel infoModel = questionSearchService.findById(id);
        if(infoModel == null) return new ResultModel(404,"错误的id");
        if(userModel.isDisabled()) return new ResultModel(0,"错误的用户状态");
        String clientIp = IPUtils.getClientIp(request);
        Long ip = IPUtils.toLong(clientIp);
        QuestionStarModel starModel = new QuestionStarModel(id, DateUtils.getTimestamp(),userModel.getId(),ip);
        try{
            int result = starService.insert(starModel);
            if(result != 1) throw new Exception("关注失败");
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
            if(result != 1) throw new Exception("取消关注失败");
            QuestionInfoModel infoModel = questionSearchService.findById(id);
            return new ResultModel(infoModel.getStarCount());
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }
}
