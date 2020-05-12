package com.itellyou.api.controller.question;

import com.itellyou.model.common.ResultModel;
import com.itellyou.util.serialize.filter.Labels;
import com.itellyou.model.question.QuestionVersionModel;
import com.itellyou.model.sys.RewardType;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.question.QuestionVersionService;
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
@RequestMapping("/question/{questionId:\\d+}/version")
public class QuestionVersionController {
    private final QuestionVersionService versionService;

    @Autowired
    public QuestionVersionController(QuestionVersionService versionService){
        this.versionService = versionService;
    }

    @GetMapping("")
    public ResultModel list(@PathVariable @NotNull Long questionId){

        List<QuestionVersionModel> listVersion = versionService.searchByQuestionId(questionId);
        return new ResultModel(listVersion,
                new Labels.LabelModel(UserInfoModel.class,"base"),
                new Labels.LabelModel(TagInfoModel.class,"base"));
    }

    @GetMapping("/{versionId:\\d+}")
    public ResultModel find(@PathVariable @NotNull Long versionId, @PathVariable @NotNull Long questionId){
        QuestionVersionModel versionModel = versionService.findByQuestionIdAndId(versionId,questionId);
        if(versionModel == null){
            return new ResultModel(0,"错误的编号");
        }
        return new ResultModel(versionModel,
                new Labels.LabelModel(UserInfoModel.class,"base"),
                new Labels.LabelModel(TagInfoModel.class,"base"));
    }

    private String getVersionHtml(QuestionVersionModel versionModel){
        StringBuilder currentString = new StringBuilder("<div>");
        currentString.append("<h2>" + versionModel.getTitle() + "</h2>");
        List<TagInfoModel> currentTagList = versionModel.getTags();
        currentString.append("<p class=\"info-layout\">");
        if(currentTagList != null && currentTagList.size() > 0){
            for(TagInfoModel tagInfo : currentTagList){
                currentString.append("<span>" + tagInfo.getName() + "</span>");
            }
            currentString.append("，");
        }
        String rewardType = "无悬赏";
        if(versionModel.getRewardType() == RewardType.CREDIT){
            rewardType = "悬赏 <span>" + versionModel.getRewardValue() + "</span> 积分";
        }
        if(versionModel.getRewardType() == RewardType.CASH){
            rewardType = "悬赏 <span>" + versionModel.getRewardValue() + "</span> 元";
        }
        currentString.append(rewardType);
        currentString.append("</p>");
        currentString.append(versionModel.getHtml());
        currentString.append("</div>");

        return currentString.toString();
    }

    @GetMapping("/{current:\\d+}...{target:\\d+}")
    public ResultModel compare(@PathVariable @NotNull Long current, @PathVariable @NotNull Long target, @PathVariable @NotNull Long questionId){
        QuestionVersionModel currentVersion = versionService.findByQuestionIdAndId(current,questionId);
        if(currentVersion == null){
            return new ResultModel(0,"错误的当前编号");
        }

        QuestionVersionModel targetVersion = versionService.findByQuestionIdAndId(target,questionId);
        if(targetVersion == null){
            return new ResultModel(0,"错误的目标编号");
        }

        Map<String,String> htmlData = new HashMap<>();
        htmlData.put("current",getVersionHtml(currentVersion));
        htmlData.put("target",getVersionHtml(targetVersion));

        return new ResultModel(htmlData);
    }
}
