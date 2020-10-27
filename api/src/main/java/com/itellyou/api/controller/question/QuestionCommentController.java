package com.itellyou.api.controller.question;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.question.QuestionCommentDetailModel;
import com.itellyou.model.question.QuestionCommentModel;
import com.itellyou.model.question.QuestionCommentVoteModel;
import com.itellyou.model.question.QuestionInfoModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.common.VoteService;
import com.itellyou.service.common.impl.VoteFactory;
import com.itellyou.service.question.QuestionCommentSearchService;
import com.itellyou.service.question.QuestionCommentService;
import com.itellyou.service.question.QuestionSingleService;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Validated
@RestController
@RequestMapping("/question/{questionId:\\d+}/comment")
public class QuestionCommentController {

    private final QuestionCommentService commentService;
    private final QuestionCommentSearchService commentSearchService;
    private final QuestionSingleService questionSingleService;
    private final VoteService<QuestionCommentVoteModel> voteService;

    public QuestionCommentController(QuestionCommentSearchService commentSearchService, QuestionCommentService commentService, QuestionSingleService questionSingleService, VoteFactory voteFactory){
        this.commentSearchService = commentSearchService;
        this.commentService = commentService;
        this.questionSingleService = questionSingleService;
        this.voteService = voteFactory.create(EntityType.QUESTION_COMMENT);
    }

    @GetMapping("/root")
    public ResultModel root(UserInfoModel userModel, @PathVariable Long questionId, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        QuestionInfoModel questionModel = questionSingleService.findById(questionId);

        if(questionModel == null || questionModel.isDisabled()){
            return new ResultModel(404,"Not found");
        }
        Long searchUserId = userModel == null ? null : userModel.getId();
        if(offset == null || offset < 0) offset = 0;
        if(limit == null) limit = 10;
        if(limit > 80){
            limit = 80;
        }

        // 查询热门评论
        Map<String,String > order = new HashMap<>();
        order.put("support_count","desc");
        List<QuestionCommentDetailModel> hostList = commentSearchService.search(questionId,null,searchUserId,null,null,true,null,null,3,null,null,null,null,null,
                order,0,10);
        int newOffset = 0;
        int newLimit = 0;
        List<QuestionCommentDetailModel> hotData = new ArrayList<>();
        if(offset < hostList.size()){
            for (QuestionCommentDetailModel hotComment : hostList){
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
        PageModel<QuestionCommentDetailModel> pageData = commentSearchService.page(questionId,new HashSet<Long>(){{add(0L);}},searchUserId,null,2,true,null,null,null,null,null,null,null,null,
                null,newOffset,newLimit);
        pageData.getData().addAll(0,hotData);
        pageData.setOffset(offset);
        pageData.setLimit(limit);
        Map<String,Object> extendData = new HashMap<>();
        extendData.put("comment_count",questionModel.getCommentCount());
        extendData.put("hot_count",hotData.size());
        pageData.setExtend(extendData);
        return new ResultModel(pageData);
    }

    @GetMapping("/{id:\\d+}/child")
    public ResultModel child(UserInfoModel userModel, @PathVariable Long questionId, @PathVariable Long id, @RequestParam(required = false) boolean hasDetail, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        QuestionInfoModel questionModel = questionSingleService.findById(questionId);
        if(questionModel == null || questionModel.isDisabled()){
            return new ResultModel(404,"Not found");
        }
        Long searchUserId = userModel == null ? null : userModel.getId();
        if(offset == null || offset < 0) offset = 0;
        if(limit == null) limit = 10;
        if(limit > 80){
            limit = 80;
        }
        // 查询评论
        PageModel<QuestionCommentDetailModel> pageData = commentSearchService.page(questionId,new HashSet<Long>(){{add(id);}},searchUserId,null,null,true,null,null,null,null,null,null,null,null,
                null,offset,limit);
        if(hasDetail){
            QuestionCommentDetailModel commentDetail = commentSearchService.getDetail(id,questionId,null,null,searchUserId,null,null,true);
            Map<String,Object> extendData = new HashMap<>();
            extendData.put("detail",commentDetail);
            pageData.setExtend(extendData);
        }
        return new ResultModel(pageData);
    }

    @PutMapping("/create")
    public ResultModel create(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long questionId, @MultiRequestBody(required = false,parseAllFields = false) Long parentId, @MultiRequestBody(required = false,parseAllFields = false) Long replyId, @MultiRequestBody String content, @MultiRequestBody String html){
        if(userModel == null) return new ResultModel(401,"未登陆");
        if(parentId == null) parentId = 0l;
        if(replyId == null) replyId = 0l;

        try {
            if(parentId > 0){
                QuestionCommentModel commentModel = commentSearchService.findById(parentId);
                if(commentModel == null) new ResultModel(0,"错误的parentId");
            }
            if(replyId > 0){
                QuestionCommentModel commentModel = commentSearchService.findById(replyId);
                if(commentModel == null) new ResultModel(0,"错误的replyId");
            }
            QuestionCommentModel commentModel = commentService.insert(questionId, parentId, replyId, content, html, userModel.getId(), IPUtils.getClientIp(request));
            if (commentModel == null) return new ResultModel(0, "评论失败");
            QuestionCommentDetailModel detailModel = commentSearchService.getDetail(commentModel.getId(),questionId,null,null,userModel.getId(),userModel.getId(),null,true);
            return new ResultModel(detailModel);
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public ResultModel delete(HttpServletRequest request,UserInfoModel userModel, @PathVariable Long id, @PathVariable Long questionId){
        if(userModel == null) return new ResultModel(401,"未登陆");
        QuestionCommentDetailModel detailModel = commentSearchService.getDetail(id,questionId,null,null,userModel.getId(),userModel.getId(),false,true);

        if(detailModel == null || !detailModel.getCreatedUserId().equals(userModel.getId()) || detailModel.isDeleted()){
            return new ResultModel(0,"错误的评论编号");
        }

        int result = commentService.updateDeleted(id,true,userModel.getId(),IPUtils.toLong(request));
        detailModel.setDeleted(true);
        detailModel.setContent("");
        detailModel.setAllowDelete(false);
        detailModel.setHtml("");
        return result == 1 ? new ResultModel(detailModel) : new ResultModel(0,"删除评论失败");
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
}
