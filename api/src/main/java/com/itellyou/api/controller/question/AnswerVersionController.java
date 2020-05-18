package com.itellyou.api.controller.question;

import com.itellyou.api.handler.TokenAccessDeniedException;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerVersionModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.question.QuestionAnswerPaidReadSearchService;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionAnswerVersionService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/question/{questionId:\\d+}/answer")
public class AnswerVersionController {
    private final QuestionAnswerVersionService versionService;
    private final QuestionAnswerSearchService searchService;
    private final QuestionAnswerPaidReadSearchService paidReadSearchService;
    private final QuestionSearchService questionSearchService;

    @Autowired
    public AnswerVersionController(QuestionAnswerVersionService versionService, QuestionAnswerSearchService searchService, QuestionAnswerPaidReadSearchService paidReadSearchService, QuestionSearchService questionSearchService){
        this.versionService = versionService;
        this.searchService = searchService;
        this.paidReadSearchService = paidReadSearchService;
        this.questionSearchService = questionSearchService;
    }

    private Long getUserAnswerId(Long userId,Long questionId){
        if(userId == null) return null;
        // 获取用户的回答
        QuestionAnswerModel answerModel = searchService.findByQuestionIdAndUserId(questionId,userId);
        if(answerModel == null || answerModel.isDeleted() || answerModel.isDisabled()){
            return null;
        }
        return answerModel.getId();
    }

    private Long check (Long answerId,Long questionId,Long userId){
        if(answerId == null){
            // 获取用户的回答
            answerId = userId == null ? null : getUserAnswerId(userId,questionId);
            if(answerId == null){
                throw new TokenAccessDeniedException(403,"无权限");
            }
        }else{
            QuestionAnswerModel answerModel = searchService.findById(answerId);
            if(answerModel == null) throw new TokenAccessDeniedException(403,"无权限");
            boolean check = paidReadSearchService.checkRead(paidReadSearchService.findByAnswerId(answerId),questionId,answerModel.getCreatedUserId(),userId);
            if(check == false){
                throw new TokenAccessDeniedException(403,"无权限");
            }
        }
        return answerId;
    }


    @GetMapping(value = {"/{answerId:\\d+}/version","/version"})
    public ResultModel list(UserInfoModel userModel , @PathVariable @NotNull Long questionId, @PathVariable(required = false) Long answerId){
        answerId = check(answerId,questionId,userModel == null ? null : userModel.getId());
        List<QuestionAnswerVersionModel> listVersion = versionService.searchByAnswerId(answerId,questionId);
        return new ResultModel(listVersion,new Labels.LabelModel(UserInfoModel.class,"base"));
    }

    @GetMapping(value = {"/{answerId:\\d+}/version/{versionId:\\d+}","/version/{versionId:\\d+}"})
    public ResultModel find(UserInfoModel userModel , @PathVariable @NotNull Long versionId, @PathVariable @NotNull Long questionId, @PathVariable(required = false) Long answerId){
        answerId = check(answerId,questionId,userModel == null ? null : userModel.getId());
        QuestionAnswerVersionModel versionModel = versionService.findByAnswerIdAndId(versionId,answerId,questionId);
        if(versionModel == null){
            return new ResultModel(0,"错误的编号");
        }
        return new ResultModel(versionModel,new Labels.LabelModel(UserInfoModel.class,"base"));
    }

    private String getVersionHtml(QuestionAnswerVersionModel versionModel){
        StringBuilder currentString = new StringBuilder("<!doctype html>");
        currentString.append(versionModel.getHtml());
        return currentString.toString();
    }

    @GetMapping(value = {"/{answerId:\\d+}/version/{current:\\d+}...{target:\\d+}","/version/{current:\\d+}...{target:\\d+}"})
    public ResultModel compare(UserInfoModel userModel , @PathVariable @NotNull Long current, @PathVariable @NotNull Long target, @PathVariable @NotNull Long questionId, @PathVariable(required = false) Long answerId){
        answerId = check(answerId,questionId,userModel == null ? null : userModel.getId());
        QuestionAnswerVersionModel currentVersion = versionService.findByAnswerIdAndId(current,answerId,questionId);
        if(currentVersion == null){
            return new ResultModel(0,"错误的当前编号");
        }

        QuestionAnswerVersionModel targetVersion = versionService.findByAnswerIdAndId(target,answerId,questionId);
        if(targetVersion == null){
            return new ResultModel(0,"错误的目标编号");
        }

        Map<String,String> htmlData = new HashMap<>();
        htmlData.put("current",getVersionHtml(currentVersion));
        htmlData.put("target",getVersionHtml(targetVersion));

        return new ResultModel(htmlData);
    }
}
