package com.itellyou.api.controller.question;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionAnswerService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping(value = {"/question/{questionId:\\d+}/answer"})
public class AnswerController {

    private final QuestionAnswerService answerService;
    private final QuestionAnswerSearchService searchService;
    private final UserDraftService draftService;

    @Autowired
    public AnswerController(QuestionAnswerService answerService,QuestionAnswerSearchService searchService,UserDraftService draftService){
        this.answerService = answerService;
        this.searchService = searchService;
        this.draftService = draftService;
    }

    @GetMapping("/list")
    public Result list(UserInfoModel userModel,@PathVariable Long questionId, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){

        Long searchUserId = userModel == null ? null : userModel.getId();
        if(offset == null || offset < 0) offset = 0;
        if(limit == null) limit = 20;
        if(limit > 80){
            limit = 80;
        }

        // 查询已采纳回答
        Map<String,String > order = new HashMap<>();
        order.put("support","desc");
        List<QuestionAnswerDetailModel> adoptList = searchService.search(questionId,searchUserId,true,true,false,true,false,null,null,
                order,0,10);
        int newOffset = 0;
        int newLimit = 0;
        List<QuestionAnswerDetailModel> adoptData = new ArrayList<>();
        if(offset < adoptList.size()){
            for (QuestionAnswerDetailModel adoptAnswer : adoptList){
                int index = adoptList.indexOf(adoptAnswer);
                if(index >= offset){
                    adoptData.add(adoptAnswer);
                }
            }
            newOffset = 0;
        }else{
            newOffset = offset - adoptList.size();
        }
        newLimit = limit - adoptData.size();

        PageModel<QuestionAnswerDetailModel> pageData = searchService.page(questionId,searchUserId, null,true,false,false,true,false,null,null,
                null,newOffset,newLimit);
        pageData.getData().addAll(0,adoptData);
        pageData.setOffset(offset);
        pageData.setLimit(limit);
        //pageData.setTotal(pageData.getTotal() + adoptData.size());
        Map<String,Object> extendData = new HashMap<>();
        extendData.put("adopts",adoptData.size());
        pageData.setExtend(extendData);
        return new Result(pageData);
    }

    @GetMapping(value = {"/{id:\\d+}"})
    public Result detail(UserInfoModel userModel ,@PathVariable Long questionId, @PathVariable Long id){
        Long searchUserId = userModel == null ? null : userModel.getId();
        QuestionAnswerDetailModel detailModel = searchService.getDetail(id,questionId,"version",searchUserId,null,true,null,false,true,false);
        if(detailModel == null || detailModel.isDeleted() || detailModel.isDisabled()) return new Result(404,"无数据");
        return new Result(detailModel);
    }

    @GetMapping("/draft") //一个问答只能有一个非禁用回答
    public Result find(UserInfoModel userModel, @PathVariable Long questionId){
        if(userModel == null){
            return new Result(401,"未登陆");
        }

        QuestionAnswerModel answerModel = searchService.findByQuestionIdAndUserId(questionId,userModel.getId());
        if(answerModel != null && !answerModel.isDisabled()){
            boolean result = draftService.exists(userModel.getId(), EntityType.ANSWER,answerModel.getId());
            Map<String,Object> userAnswerMap = new HashMap<>();
            userAnswerMap.put("published",answerModel.isPublished());
            userAnswerMap.put("deleted",answerModel.isDeleted());
            userAnswerMap.put("adopted",answerModel.isAdopted());
            userAnswerMap.put("id",answerModel.getId());
            userAnswerMap.put("draft",result);
            return new Result(userAnswerMap);
        }
        return new Result(404,"Not find");
    }

    @DeleteMapping("/{id:\\d+}/draft")
    public Result deleteDraft(UserInfoModel userModel, @PathVariable Long id){
        if(userModel == null){
            return new Result(401,"未登陆");
        }
        int result = draftService.delete(userModel.getId(), EntityType.ANSWER,id);
        if(result != 1) return new Result(0,"删除失败");
        return new Result();
    }

    @DeleteMapping("/{id:\\d+}")
    public Result delete(UserInfoModel userModel,@PathVariable Long questionId, @PathVariable Long id){
        if(userModel == null){
            return new Result(401,"未登陆");
        }
        try {
            QuestionAnswerDetailModel answerModel = answerService.delete(id,questionId,userModel.getId());
            if(answerModel == null) return new Result(0,"删除失败");
            draftService.delete(userModel.getId(), EntityType.ANSWER,id);
            return new Result(answerModel);
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }

    @PostMapping("/{id:\\d+}/revoke")
    public Result revokeDelete(UserInfoModel userModel,@PathVariable Long questionId, @PathVariable Long id){
        if(userModel == null){
            return new Result(401,"未登陆");
        }
        try {
            QuestionAnswerDetailModel answerModel = answerService.revokeDelete(id,questionId,userModel.getId());
            if(answerModel == null) return new Result(0,"撤销删除失败");
            return new Result(answerModel);
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }

    @PostMapping(value = { "/{id:\\d+}/{type:support}","/{id:\\d+}/{type:oppose}"})
    public Result vote(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long id, @PathVariable String type){
        if(userModel == null){
            return new Result(401,"未登陆");
        }
        Map<String,Object> data = answerService.updateVote(VoteType.valueOf(type.toUpperCase()),id,userModel.getId(), IPUtils.getClientIp(request));
        if(data == null){
            return new Result(0,"更新失败");
        }
        return new Result(data);
    }
}
