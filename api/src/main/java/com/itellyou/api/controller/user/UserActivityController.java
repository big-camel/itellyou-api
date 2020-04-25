package com.itellyou.api.controller.user;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.article.ArticleCommentDetailModel;
import com.itellyou.model.question.QuestionAnswerCommentDetailModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionCommentDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserOperationalDetailModel;
import com.itellyou.service.user.UserActivityService;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/user/activity")
public class UserActivityController {
    private final UserActivityService activityService;

    public UserActivityController(UserActivityService activityService){
        this.activityService = activityService;
    }

    @GetMapping("")
    public Result list(UserInfoModel userModel, @RequestParam(required = false) Long id, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(id == null && userModel == null) return new Result(401,"未登陆");
        if(id == null) id = userModel.getId();
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<UserOperationalDetailModel> pageData = activityService.page(null,null,id,null,null,null,order,offset,limit);
        return new Result(pageData ,
                new Labels.LabelModel(QuestionAnswerDetailModel.class,"base","question"),
                new Labels.LabelModel(QuestionCommentDetailModel.class,"base","question"),
                new Labels.LabelModel(QuestionAnswerCommentDetailModel.class,"base","answer"),
                new Labels.LabelModel(ArticleCommentDetailModel.class,"base","article"));
    }
}
