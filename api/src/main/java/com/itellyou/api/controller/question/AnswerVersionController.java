package com.itellyou.api.controller.question;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerVersionModel;
import com.itellyou.model.question.QuestionVersionModel;
import com.itellyou.model.reward.RewardType;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionAnswerService;
import com.itellyou.service.question.QuestionAnswerVersionService;
import com.itellyou.service.question.QuestionVersionService;
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
    private final QuestionAnswerService answerService;
    private final QuestionAnswerSearchService searchService;

    @Autowired
    public AnswerVersionController(QuestionAnswerService answerService, QuestionAnswerVersionService versionService,QuestionAnswerSearchService searchService){
        this.versionService = versionService;
        this.answerService = answerService;
        this.searchService = searchService;
    }

    private Long getUserAnswerId(UserInfoModel userModel,Long questionId){
        if(userModel == null) return null;
        // 获取用户的回答
        QuestionAnswerModel answerModel = searchService.findByQuestionIdAndUserId(questionId,userModel.getId());
        if(answerModel == null || answerModel.isDeleted() || answerModel.isDisabled()){
            return null;
        }
        return answerModel.getId();
    }

    @GetMapping(value = {"/{answerId:\\d+}/version","/version"})
    public Result list(UserInfoModel userModel , @PathVariable @NotNull Long questionId, @PathVariable(required = false) Long answerId){
        if(answerId == null){
            // 获取用户的回答
            answerId = getUserAnswerId(userModel,questionId);
            if(answerId == null){
                return new Result(404,"Not find");
            }
        }

        List<QuestionAnswerVersionModel> listVersion = versionService.searchByAnswerId(answerId,questionId);
        return new Result(listVersion,new Labels.LabelModel(UserInfoModel.class,"base"));
    }

    @GetMapping(value = {"/{answerId:\\d+}/version/{versionId:\\d+}","/version/{versionId:\\d+}"})
    public Result find(UserInfoModel userModel ,@PathVariable @NotNull Long versionId, @PathVariable @NotNull Long questionId,@PathVariable(required = false) Long answerId){
        if(answerId == null){
            // 获取用户的回答
            answerId = getUserAnswerId(userModel,questionId);
            if(answerId == null){
                return new Result(404,"Not find");
            }
        }
        QuestionAnswerVersionModel versionModel = versionService.findByAnswerIdAndId(versionId,answerId,questionId);
        if(versionModel == null){
            return new Result(0,"错误的编号");
        }
        return new Result(versionModel,new Labels.LabelModel(UserInfoModel.class,"base"));
    }

    private String getVersionHtml(QuestionAnswerVersionModel versionModel){
        StringBuilder currentString = new StringBuilder("<!doctype html>");
        currentString.append(versionModel.getHtml());
        return currentString.toString();
    }

    @GetMapping(value = {"/{answerId:\\d+}/version/{current:\\d+}...{target:\\d+}","/version/{current:\\d+}...{target:\\d+}"})
    public Result compare(UserInfoModel userModel ,@PathVariable @NotNull Long current,@PathVariable @NotNull Long target,@PathVariable @NotNull Long questionId,@PathVariable(required = false) Long answerId){
        if(answerId == null){
            // 获取用户的回答
            answerId = getUserAnswerId(userModel,questionId);
            if(answerId == null){
                return new Result(404,"Not find");
            }
        }
        QuestionAnswerVersionModel currentVersion = versionService.findByAnswerIdAndId(current,answerId,questionId);
        if(currentVersion == null){
            return new Result(0,"错误的当前编号");
        }

        QuestionAnswerVersionModel targetVersion = versionService.findByAnswerIdAndId(target,answerId,questionId);
        if(targetVersion == null){
            return new Result(0,"错误的目标编号");
        }

        Map<String,String> htmlData = new HashMap<>();
        htmlData.put("current",getVersionHtml(currentVersion));
        htmlData.put("target",getVersionHtml(targetVersion));

        return new Result(htmlData);
    }
}
