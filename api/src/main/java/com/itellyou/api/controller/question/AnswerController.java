package com.itellyou.api.controller.question;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.question.QuestionAnswerVoteModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.common.VoteService;
import com.itellyou.service.common.impl.VoteFactory;
import com.itellyou.service.question.QuestionAnswerPaidReadSearchService;
import com.itellyou.service.question.QuestionAnswerPaidReadService;
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

    private final VoteService<QuestionAnswerVoteModel> voteService;
    private final QuestionAnswerService answerService;
    private final QuestionAnswerSearchService searchService;
    private final UserDraftService draftService;
    private final QuestionAnswerPaidReadService answerPaidReadService;
    private final QuestionAnswerPaidReadSearchService answerPaidReadSearchService;

    @Autowired
    public AnswerController(QuestionAnswerService answerService, QuestionAnswerSearchService searchService, UserDraftService draftService, VoteFactory voteFactory, QuestionAnswerPaidReadService answerPaidReadService, QuestionAnswerPaidReadSearchService answerPaidReadSearchService){
        this.voteService = voteFactory.create(EntityType.ANSWER);
        this.answerService = answerService;
        this.searchService = searchService;
        this.draftService = draftService;
        this.answerPaidReadService = answerPaidReadService;
        this.answerPaidReadSearchService = answerPaidReadSearchService;
    }

    @GetMapping("/list")
    public ResultModel list(UserInfoModel userModel, @PathVariable Long questionId, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){

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
        Map<String,Object> extendData = new HashMap<>();
        extendData.put("adopts",adoptData.size());
        pageData.setExtend(extendData);
        return new ResultModel(pageData);
    }

    @GetMapping(value = {"/{id:\\d+}"})
    public ResultModel detail(UserInfoModel userModel , @PathVariable Long questionId, @PathVariable Long id){
        Long searchUserId = userModel == null ? null : userModel.getId();
        QuestionAnswerDetailModel detailModel = searchService.getDetail(id,questionId,"version",searchUserId,null,true,null,false,true,false);
        if(detailModel == null || detailModel.isDeleted() || detailModel.isDisabled()) return new ResultModel(404,"无数据");
        return new ResultModel(detailModel);
    }

    @GetMapping("/draft") //一个问答只能有一个非禁用回答
    public ResultModel find(UserInfoModel userModel, @PathVariable Long questionId){
        if(userModel == null){
            return new ResultModel(401,"未登陆");
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
            return new ResultModel(userAnswerMap);
        }
        return new ResultModel(404,"Not find");
    }

    @DeleteMapping("/{id:\\d+}/draft")
    public ResultModel deleteDraft(UserInfoModel userModel, @PathVariable Long id){
        if(userModel == null){
            return new ResultModel(401,"未登陆");
        }
        int result = draftService.delete(userModel.getId(), EntityType.ANSWER,id);
        if(result != 1) return new ResultModel(0,"删除失败");
        return new ResultModel();
    }

    @DeleteMapping("/{id:\\d+}")
    public ResultModel delete(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long questionId, @PathVariable Long id){
        if(userModel == null){
            return new ResultModel(401,"未登陆");
        }
        try {
            QuestionAnswerDetailModel answerModel = answerService.delete(id,questionId,userModel.getId(), IPUtils.toLong(IPUtils.getClientIp(request)));
            if(answerModel == null) return new ResultModel(0,"删除失败");
            draftService.delete(userModel.getId(), EntityType.ANSWER,id);
            return new ResultModel(answerModel);
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PostMapping("/{id:\\d+}/revoke")
    public ResultModel revokeDelete(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long questionId, @PathVariable Long id){
        if(userModel == null){
            return new ResultModel(401,"未登陆");
        }
        try {
            QuestionAnswerDetailModel answerModel = answerService.revokeDelete(id,questionId,userModel.getId(), IPUtils.toLong(IPUtils.getClientIp(request)));
            if(answerModel == null) return new ResultModel(0,"撤销删除失败");
            return new ResultModel(answerModel);
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @PostMapping(value = { "/{id:\\d+}/{type:support}","/{id:\\d+}/{type:oppose}"})
    public ResultModel vote(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long id, @PathVariable String type){
        if(userModel == null){
            return new ResultModel(401,"未登陆");
        }
        Map<String,Object> data = voteService.doVote(VoteType.valueOf(type.toUpperCase()),id,userModel.getId(), IPUtils.toLong(IPUtils.getClientIp(request)));
        if(data == null){
            return new ResultModel(0,"更新失败");
        }
        return new ResultModel(data);
    }

    @PostMapping("/{id:\\d+}/paidread")
    public ResultModel doPaidRead(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long id){
        try{
            UserBankLogModel logModel = answerPaidReadService.doPaidRead(id,userModel.getId(), IPUtils.toLong(request));
            return new ResultModel(logModel);
        }catch (Exception e){
            return new ResultModel(500,e.getLocalizedMessage());
        }
    }
}
