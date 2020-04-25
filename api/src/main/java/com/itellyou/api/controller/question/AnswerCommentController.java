package com.itellyou.api.controller.question;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionAnswerCommentDetailModel;
import com.itellyou.model.question.QuestionAnswerCommentModel;
import com.itellyou.model.question.QuestionAnswerModel;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.question.QuestionAnswerCommentSearchService;
import com.itellyou.service.question.QuestionAnswerCommentService;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/question/{questionId:\\d+}/answer/{answerId:\\d+}/comment")
public class AnswerCommentController {

    private final QuestionAnswerCommentService commentService;
    private final QuestionAnswerCommentSearchService commentSearchService;
    private final QuestionAnswerSearchService searchService;

    public AnswerCommentController(QuestionAnswerCommentSearchService commentSearchService, QuestionAnswerCommentService commentService,QuestionAnswerSearchService searchService){
        this.commentSearchService = commentSearchService;
        this.commentService = commentService;
        this.searchService = searchService;
    }

    @GetMapping("/root")
    public Result root(UserInfoModel userModel, @PathVariable Long questionId, @PathVariable Long answerId, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        QuestionAnswerModel answerModel = searchService.findById(answerId);

        if(answerModel == null || answerModel.isDisabled() || !answerModel.getQuestionId().equals(questionId)){
            return new Result(404,"Not found");
        }
        Long searchUserId = userModel == null ? null : userModel.getId();
        if(offset == null || offset < 0) offset = 0;
        if(limit == null) limit = 10;
        if(limit > 80){
            limit = 80;
        }

        // 查询热门评论
        Map<String,String > order = new HashMap<>();
        order.put("support","desc");
        order.put("created_time","asc");
        List<QuestionAnswerCommentDetailModel> hostList = commentSearchService.search(answerId,null,searchUserId,null,null,null,null,3,null,null,null,null,null,
                order,0,10);
        int newOffset = 0;
        int newLimit = 0;
        List<QuestionAnswerCommentDetailModel> hotData = new ArrayList<>();
        if(offset < hostList.size()){
            for (QuestionAnswerCommentDetailModel hotComment : hostList){
                int index = hostList.indexOf(hotComment);
                if(index >= offset){
                    hotComment.setHot(true);
                    hotData.add(hotComment);
                }
            }
            newOffset = 0;
        }else{
            newOffset = offset - hostList.size();
        }
        newLimit = limit - hotData.size();
        // 查询评论
        PageModel<QuestionAnswerCommentDetailModel> pageData = commentSearchService.page(answerId,0L,searchUserId,null,2,null,null,null,null,null,null,null,null,
                order,newOffset,newLimit);
        pageData.getData().addAll(0,hotData);
        pageData.setOffset(offset);
        pageData.setLimit(limit);
        Map<String,Object> extendData = new HashMap<>();
        extendData.put("comments",answerModel.getComments());
        extendData.put("hots",hotData.size());
        pageData.setExtend(extendData);
        return new Result(pageData);
    }

    @GetMapping("/{id:\\d+}/child")
    public Result child(UserInfoModel userModel, @PathVariable Long questionId, @PathVariable Long answerId,@PathVariable Long id, @RequestParam(required = false) boolean hasDetail, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        QuestionAnswerModel answerModel = searchService.findById(answerId);
        if(answerModel == null || answerModel.isDisabled() || !answerModel.getQuestionId().equals(questionId)){
            return new Result(404,"Not found");
        }
        Long searchUserId = userModel == null ? null : userModel.getId();
        if(offset == null || offset < 0) offset = 0;
        if(limit == null) limit = 10;
        if(limit > 80){
            limit = 80;
        }
        Map<String,String > order = new HashMap<>();
        order.put("support","desc");
        order.put("created_time","asc");
        // 查询评论
        PageModel<QuestionAnswerCommentDetailModel> pageData = commentSearchService.page(answerId,id,searchUserId,null,null,null,null,null,null,null,null,null,null,
                order,offset,limit);
        if(hasDetail){
            QuestionAnswerCommentDetailModel commentDetail = commentSearchService.getDetail(id,answerId,null,null,searchUserId,null,null);
            Map<String,Object> extendData = new HashMap<>();
            extendData.put("detail",commentDetail);
            pageData.setExtend(extendData);
        }
        return new Result(pageData);
    }

    @PutMapping("/create")
    public Result create(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long questionId, @PathVariable Long answerId,@MultiRequestBody(required = false,parseAllFields = false) Long parentId,@MultiRequestBody(required = false,parseAllFields = false) Long replyId, @MultiRequestBody String content, @MultiRequestBody String html){
        if(userModel == null) return new Result(401,"未登陆");
        if(parentId == null) parentId = 0l;
        if(replyId == null) replyId = 0l;

        try {
            if(parentId > 0){
                QuestionAnswerCommentModel commentModel = commentSearchService.findById(parentId);
                if(commentModel == null) new Result(0,"错误的parentId");
            }
            if(replyId > 0){
                QuestionAnswerCommentModel commentModel = commentSearchService.findById(replyId);
                if(commentModel == null) new Result(0,"错误的replyId");
            }
            QuestionAnswerCommentModel commentModel = commentService.insert(answerId, parentId, replyId, content, html, userModel.getId(), IPUtils.getClientIp(request));
            if (commentModel == null) return new Result(0, "评论失败");
            QuestionAnswerCommentDetailModel detailModel = commentSearchService.getDetail(commentModel.getId(),answerId,null,null,userModel.getId(),userModel.getId(),null);
            return new Result(detailModel);
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public Result delete(UserInfoModel userModel,@PathVariable Long id,@PathVariable Long answerId){
        if(userModel == null) return new Result(401,"未登陆");
        QuestionAnswerCommentDetailModel detailModel = commentSearchService.getDetail(id,answerId,null,null,userModel.getId(),userModel.getId(),false);

        if(detailModel == null || !detailModel.getCreatedUserId().equals(userModel.getId()) || detailModel.isDeleted()){
            return new Result(0,"错误的评论编号");
        }

        int result = commentService.updateDeleted(id,true);
        detailModel.setDeleted(true);
        detailModel.setContent("");
        detailModel.setAllowDelete(false);
        detailModel.setHtml("");
        return result == 1 ? new Result(detailModel) : new Result(0,"删除评论失败");
    }

    @PostMapping(value = { "/{id:\\d+}/{type:support}","/{id:\\d+}/{type:oppose}"})
    public Result vote(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long id, @PathVariable String type){
        if(userModel == null){
            return new Result(401,"未登陆");
        }
        Map<String,Object> data = commentService.updateVote(VoteType.valueOf(type.toUpperCase()),id,userModel.getId(), IPUtils.getClientIp(request));
        if(data == null){
            return new Result(0,"更新失败");
        }
        return new Result(data);
    }
}
