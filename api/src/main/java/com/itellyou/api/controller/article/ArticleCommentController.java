package com.itellyou.api.controller.article;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.article.ArticleCommentDetailModel;
import com.itellyou.model.article.ArticleCommentModel;
import com.itellyou.model.article.ArticleInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.article.ArticleCommentSearchService;
import com.itellyou.service.article.ArticleCommentService;
import com.itellyou.service.article.ArticleSearchService;
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
@RequestMapping("/article/{articleId:\\d+}/comment")
public class ArticleCommentController {

    private final ArticleCommentService commentService;
    private final ArticleCommentSearchService commentSearchService;
    private final ArticleSearchService searchService;

    public ArticleCommentController(ArticleSearchService searchService,ArticleCommentSearchService commentSearchService, ArticleCommentService commentService){
        this.searchService = searchService;
        this.commentSearchService = commentSearchService;
        this.commentService = commentService;
    }

    @GetMapping("/root")
    public Result root(UserInfoModel userModel, @PathVariable Long articleId,@RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        ArticleInfoModel articleModel = searchService.findById(articleId);

        if(articleModel == null || articleModel.isDisabled()){
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
        List<ArticleCommentDetailModel> hostList = commentSearchService.search(articleId,null,searchUserId,null,null,null,null,3,null,null,null,null,null,
                order,0,10);
        int newOffset = 0;
        int newLimit = 0;
        List<ArticleCommentDetailModel> hotData = new ArrayList<>();
        if(offset < hostList.size()){
            for (ArticleCommentDetailModel hotComment : hostList){
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
        PageModel<ArticleCommentDetailModel> pageData = commentSearchService.page(articleId,0L,searchUserId,null,2,null,null,null,null,null,null,null,null,
                null,newOffset,newLimit);
        pageData.getData().addAll(0,hotData);
        pageData.setOffset(offset);
        pageData.setLimit(limit);
        Map<String,Object> extendData = new HashMap<>();
        extendData.put("comments",articleModel.getCommentCount());
        extendData.put("hots",hotData.size());
        pageData.setExtend(extendData);
        return new Result(pageData);
    }

    @GetMapping("/{id:\\d+}/child")
    public Result child(UserInfoModel userModel, @PathVariable Long articleId,@PathVariable Long id, @RequestParam(required = false) boolean hasDetail, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        ArticleInfoModel articleModel = searchService.findById(articleId);
        if(articleModel == null || articleModel.isDisabled()){
            return new Result(404,"Not found");
        }
        Long searchUserId = userModel == null ? null : userModel.getId();
        if(offset == null || offset < 0) offset = 0;
        if(limit == null) limit = 10;
        if(limit > 80){
            limit = 80;
        }
        // 查询评论
        PageModel<ArticleCommentDetailModel> pageData = commentSearchService.page(articleId,id,searchUserId,null,null,null,null,null,null,null,null,null,null,
                null,offset,limit);
        if(hasDetail){
            ArticleCommentDetailModel commentDetail = commentSearchService.getDetail(id,articleId,null,null,searchUserId,null,null);
            Map<String,Object> extendData = new HashMap<>();
            extendData.put("detail",commentDetail);
            pageData.setExtend(extendData);
        }
        return new Result(pageData);
    }

    @PutMapping("/create")
    public Result create(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long articleId, @MultiRequestBody(required = false,parseAllFields = false) Long parentId,@MultiRequestBody(required = false,parseAllFields = false) Long replyId, @MultiRequestBody String content, @MultiRequestBody String html){
        if(userModel == null) return new Result(401,"未登陆");
        if(parentId == null) parentId = 0l;
        if(replyId == null) replyId = 0l;

        try {
            if(parentId > 0){
                ArticleCommentModel commentModel = commentSearchService.findById(parentId);
                if(commentModel == null) new Result(0,"错误的parentId");
            }
            if(replyId > 0){
                ArticleCommentModel commentModel = commentSearchService.findById(replyId);
                if(commentModel == null) new Result(0,"错误的replyId");
            }
            ArticleCommentModel commentModel = commentService.insert(articleId, parentId, replyId, content, html, userModel.getId(), IPUtils.getClientIp(request));
            if (commentModel == null) return new Result(0, "评论失败");
            ArticleCommentDetailModel detailModel = commentSearchService.getDetail(commentModel.getId(),articleId,null,null,userModel.getId(),userModel.getId(),null);
            return new Result(detailModel);
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public Result delete(UserInfoModel userModel,@PathVariable Long id,@PathVariable Long articleId){
        if(userModel == null) return new Result(401,"未登陆");
        ArticleCommentDetailModel detailModel = commentSearchService.getDetail(id,articleId,null,null,userModel.getId(),userModel.getId(),false);

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
