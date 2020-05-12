package com.itellyou.api.controller.user;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.service.user.UserDraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/user/question")
public class UserQuestionController {

    private final QuestionSearchService questionSearchService;

    @Autowired
    public UserQuestionController(QuestionSearchService questionSearchService, UserDraftService draftService){
        this.questionSearchService = questionSearchService;
    }

    @GetMapping("")
    public ResultModel list(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new ResultModel(401,"未登陆");
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<QuestionDetailModel> pageData = questionSearchService.page(userModel.getId(),userModel.getId(),false,false,false,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,order,offset,limit);
        return new ResultModel(pageData);
    }
}
