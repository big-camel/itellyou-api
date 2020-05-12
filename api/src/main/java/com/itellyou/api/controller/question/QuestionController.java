package com.itellyou.api.controller.question;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.question.QuestionAnswerService;
import com.itellyou.service.question.QuestionInfoService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.util.BrowserUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.OsUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/question")
public class QuestionController {

    private final QuestionInfoService questionService;
    private final QuestionSearchService questionSearchService;
    private final QuestionAnswerService answerService;
    private final UserDraftService draftService;

    @Autowired
    public QuestionController(QuestionInfoService questionService,QuestionSearchService questionSearchService, QuestionAnswerService answerService, UserDraftService draftService){
        this.questionService = questionService;
        this.questionSearchService = questionSearchService;
        this.answerService = answerService;
        this.draftService = draftService;
    }

    @GetMapping("/{id:\\d+}/view")
    public ResultModel view(HttpServletRequest request, UserInfoModel userInfo, @PathVariable Long id){
        QuestionDetailModel detailModel = questionSearchService.getDetail(id);
        if(detailModel == null){
            return new ResultModel(404,"错误的编号");
        }
        String ip = IPUtils.getClientIp(request);
        Long longIp = IPUtils.toLong(ip);
        String os = OsUtils.getClientOs(request);
        String browser = BrowserUtils.getClientBrowser(request);
        if(browser == "Robot/Spider"){
            return new ResultModel(0,"Robot/Spider Error");
        }
        Long userId = userInfo == null ? 0 : userInfo.getId();
        int result = questionService.updateView(userId,id,longIp,os,browser);
        if(result == 1) return new ResultModel();
        return new ResultModel(0,"更新失败");
    }

    @PostMapping("/{id:\\d+}/adopt")
    public ResultModel adopt(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long id, @MultiRequestBody(value = "answer_id") @NotNull Long answerId){
        if(userModel == null) return new ResultModel(401,"未登陆");

        try{
            QuestionDetailModel detailModel = answerService.adopt(answerId,userModel.getId(),IPUtils.getClientIp(request));
            if(detailModel == null ) return new ResultModel(0,"采纳失败");
            return new ResultModel(detailModel);
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @GetMapping("/{id:\\d+}/user_draft")
    public ResultModel find(UserInfoModel userModel, @PathVariable Long id){
        if(userModel == null){
            return new ResultModel(401,"未登陆");
        }

        QuestionInfoModel infoModel = questionSearchService.findById(id);
        if(infoModel != null && !infoModel.isDisabled()){
            boolean result = draftService.exists(userModel.getId(), EntityType.QUESTION,infoModel.getId());
            Map<String,Object> userAnswerMap = new HashMap<>();
            userAnswerMap.put("published",infoModel.isPublished());
            userAnswerMap.put("deleted",infoModel.isDeleted());
            userAnswerMap.put("adopted",infoModel.isAdopted());
            userAnswerMap.put("id",infoModel.getId());
            userAnswerMap.put("draft",result);
            return new ResultModel(userAnswerMap);
        }
        return new ResultModel(404,"Not find");
    }

    @DeleteMapping("/{id:\\d+}/user_draft")
    public ResultModel deleteDraft(UserInfoModel userModel, @PathVariable Long id){
        if(userModel == null){
            return new ResultModel(401,"未登陆");
        }
        int result = draftService.delete(userModel.getId(), EntityType.QUESTION,id);
        if(result != 1) return new ResultModel(0,"删除失败");
        return new ResultModel();
    }

    @DeleteMapping("{id:\\d+}")
    public ResultModel delete(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long id){
        if(userModel == null) return new ResultModel(401,"未登陆");
        int result = questionService.updateDeleted(true,id,userModel.getId(),IPUtils.toLong(IPUtils.getClientIp(request)));
        if(result == 1) {
            return new ResultModel();
        }
        return new ResultModel(0,"删除失败");
    }
}
