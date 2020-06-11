package com.itellyou.api.controller.user;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Validated
@RestController
@RequestMapping("/user/answer")
public class UserAnswerController {

    private final QuestionAnswerSearchService answerSearchService;
    private final QuestionSearchService questionSearchService;

    public UserAnswerController(QuestionAnswerSearchService answerSearchService, QuestionSearchService questionSearchService){
        this.answerSearchService = answerSearchService;
        this.questionSearchService = questionSearchService;
    }

    @GetMapping("")
    public ResultModel list(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new ResultModel(401,"未登陆");
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<QuestionAnswerDetailModel> pageData = answerSearchService.page(null,null,"draft",userModel.getId(),userModel.getId(),false,null,false,null,false,null,null,null,null,null,null,null,null,null,null,null,null,null,order,offset,limit);
        HashSet<Long> questionIds = new LinkedHashSet<>();
        for (QuestionAnswerDetailModel detailModel : pageData.getData()){
            if(!questionIds.contains(detailModel.getQuestionId())){
                questionIds.add(detailModel.getQuestionId());
            }
        }
        List<QuestionDetailModel> questionDetailModels = questionSearchService.search(questionIds,null,null,userModel.getId(),false,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        for (QuestionAnswerDetailModel detailModel : pageData.getData()){
            for (QuestionDetailModel questionDetailModel : questionDetailModels){
                if(questionDetailModel.getId().equals(detailModel.getQuestionId())){
                    detailModel.setQuestion(questionDetailModel);
                    break;
                }
            }
        }
        return new ResultModel(pageData,new Labels.LabelModel(QuestionAnswerDetailModel.class,"base","question"));
    }
}
