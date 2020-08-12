package com.itellyou.api.controller.software;

import com.itellyou.model.software.SoftwareCommentDetailModel;
import com.itellyou.model.software.SoftwareCommentModel;
import com.itellyou.model.software.SoftwareCommentVoteModel;
import com.itellyou.model.software.SoftwareInfoModel;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.VoteType;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.software.SoftwareCommentSearchService;
import com.itellyou.service.software.SoftwareCommentService;
import com.itellyou.service.software.SoftwareSingleService;
import com.itellyou.service.common.VoteService;
import com.itellyou.service.common.impl.VoteFactory;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Validated
@RestController
@RequestMapping("/software/{softwareId:\\d+}/comment")
public class SoftwareCommentController {

    private final SoftwareCommentService commentService;
    private final SoftwareCommentSearchService commentSearchService;
    private final SoftwareSingleService searchService;
    private final VoteService<SoftwareCommentVoteModel> voteService;

    public SoftwareCommentController(SoftwareSingleService searchService, SoftwareCommentSearchService commentSearchService, SoftwareCommentService commentService, VoteFactory voteFactory){
        this.searchService = searchService;
        this.commentSearchService = commentSearchService;
        this.commentService = commentService;
        this.voteService = voteFactory.create(EntityType.SOFTWARE_COMMENT);
    }

    @GetMapping("/root")
    public ResultModel root(UserInfoModel userModel, @PathVariable Long softwareId, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        SoftwareInfoModel softwareModel = searchService.findById(softwareId);

        if(softwareModel == null || softwareModel.isDisabled()){
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
        order.put("support","desc");
        List<SoftwareCommentDetailModel> hostList = commentSearchService.search(softwareId,null,searchUserId,null,null,true,null,null,3,null,null,null,null,null,
                order,0,10);

        int newOffset = 0;
        int newLimit = 0;
        List<SoftwareCommentDetailModel> hotData = new ArrayList<>();
        if(offset < hostList.size()){
            for (SoftwareCommentDetailModel hotComment : hostList){
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
        PageModel<SoftwareCommentDetailModel> pageData = commentSearchService.page(softwareId,new HashSet<Long>(){{add(0L);}},searchUserId,null,2,true,null,null,null,null,null,null,null,null,
                null,newOffset,newLimit);

        pageData.getData().addAll(0,hotData);
        pageData.setOffset(offset);
        pageData.setLimit(limit);
        Map<String,Object> extendData = new HashMap<>();
        extendData.put("comments",softwareModel.getCommentCount());
        extendData.put("hots",hotData.size());
        pageData.setExtend(extendData);
        return new ResultModel(pageData);
    }

    @GetMapping("/{id:\\d+}/child")
    public ResultModel child(UserInfoModel userModel, @PathVariable Long softwareId, @PathVariable Long id, @RequestParam(required = false) boolean hasDetail, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        SoftwareInfoModel softwareModel = searchService.findById(softwareId);
        if(softwareModel == null || softwareModel.isDisabled()){
            return new ResultModel(404,"Not found");
        }
        Long searchUserId = userModel == null ? null : userModel.getId();
        if(offset == null || offset < 0) offset = 0;
        if(limit == null) limit = 10;
        if(limit > 80){
            limit = 80;
        }
        // 查询评论
        PageModel<SoftwareCommentDetailModel> pageData = commentSearchService.page(softwareId,new HashSet<Long>(){{add(id);}},searchUserId,null,null,true,null,null,null,null,null,null,null,null,
                null,offset,limit);
        if(hasDetail){
            SoftwareCommentDetailModel commentDetail = commentSearchService.getDetail(id,softwareId,null,null,searchUserId,null,null,true);
            Map<String,Object> extendData = new HashMap<>();
            extendData.put("detail",commentDetail);
            pageData.setExtend(extendData);
        }
        return new ResultModel(pageData);
    }

    @PutMapping("/create")
    public ResultModel create(HttpServletRequest request, UserInfoModel userModel, @PathVariable Long softwareId, @MultiRequestBody(required = false,parseAllFields = false) Long parentId, @MultiRequestBody(required = false,parseAllFields = false) Long replyId, @MultiRequestBody String content, @MultiRequestBody String html){
        if(userModel == null) return new ResultModel(401,"未登陆");
        if(parentId == null) parentId = 0l;
        if(replyId == null) replyId = 0l;

        try {
            if(parentId > 0){
                SoftwareCommentModel commentModel = commentSearchService.findById(parentId);
                if(commentModel == null) new ResultModel(0,"错误的parentId");
            }
            if(replyId > 0){
                SoftwareCommentModel commentModel = commentSearchService.findById(replyId);
                if(commentModel == null) new ResultModel(0,"错误的replyId");
            }
            SoftwareCommentModel commentModel = commentService.insert(softwareId, parentId, replyId, content, html, userModel.getId(), IPUtils.toLong(request),true);
            if (commentModel == null) return new ResultModel(0, "评论失败");
            SoftwareCommentDetailModel detailModel = commentSearchService.getDetail(commentModel.getId(),softwareId,null,null,userModel.getId(),userModel.getId(),null,true);
            return new ResultModel(detailModel);
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public ResultModel delete(HttpServletRequest request,UserInfoModel userModel, @PathVariable Long id, @PathVariable Long softwareId){
        if(userModel == null) return new ResultModel(401,"未登陆");
        SoftwareCommentDetailModel detailModel = commentSearchService.getDetail(id,softwareId,null,null,userModel.getId(),userModel.getId(),false,true);

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
