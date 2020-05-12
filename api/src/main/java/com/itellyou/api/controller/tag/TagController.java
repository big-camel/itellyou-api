package com.itellyou.api.controller.tag;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.tag.TagGroupService;
import com.itellyou.service.tag.TagInfoService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.user.UserDraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagInfoService tagService;
    private final TagSearchService searchService;
    private final TagGroupService groupService;
    private final UserDraftService draftService;

    @Autowired
    public TagController(TagInfoService tagService,TagSearchService searchService,TagGroupService groupService,UserDraftService draftService){
        this.tagService = tagService;
        this.searchService = searchService;
        this.groupService = groupService;
        this.draftService = draftService;
    }

    @GetMapping("/search")
    public ResultModel search(@RequestParam("w") @NotBlank String word){
        return new ResultModel(searchService.search(word,0,10));
    }

    @GetMapping("/group")
    public ResultModel group(){
        return new ResultModel(groupService.page(null,null,null,false,true,null,null,null,null,null,0,100));
    }

    @GetMapping("/list")
    public ResultModel list(UserInfoModel userModel, @RequestParam(required = false,defaultValue = "") String type, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        Long searchUserId = userModel == null ? null : userModel.getId();
        Map<String,String > order;
        switch (type) {
            case "hot":
                order = new HashMap<>();
                order.put("question_count", "desc");
                order.put("article_count", "desc");
                order.put("star_count", "desc");
                return new ResultModel(searchService.page(null,null,null,null,searchUserId,true,false,true,null,null,null,null,null,null,null,null,null,order,offset,limit));
            default:
                return new ResultModel(searchService.page(null,null,null,null,searchUserId,true,false,true,null,null,null,null,null,null,null,null,null,null,offset,limit));
        }
    }

    @GetMapping("/{id:\\d+}")
    public ResultModel detail(UserInfoModel userModel, @PathVariable Long id){
        Long searchUserId = userModel == null ? null : userModel.getId();
        TagDetailModel detailModel = searchService.getDetail(id,null,null,searchUserId,true);
        if(detailModel == null|| detailModel.isDisabled()) return  new ResultModel(404,"错误的编号");
        return new ResultModel(detailModel);
    }

    @GetMapping("/query")
    public ResultModel query(@RequestParam String name){
        TagInfoModel tagModel = searchService.findByName(name);
        if(tagModel == null) return new ResultModel(0,"无记录");
        return new ResultModel(tagModel);
    }

    @GetMapping("/{id:\\d+}/user_draft")
    public ResultModel find(UserInfoModel userModel, @PathVariable Long id){
        if(userModel == null){
            return new ResultModel(401,"未登陆");
        }

        TagInfoModel infoModel = searchService.findById(id);
        if(infoModel != null && !infoModel.isDisabled()){
            boolean result = draftService.exists(userModel.getId(), EntityType.TAG,infoModel.getId());
            Map<String,Object> userAnswerMap = new HashMap<>();
            userAnswerMap.put("published",infoModel.isPublished());
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
        int result = draftService.delete(userModel.getId(), EntityType.TAG,id);
        if(result != 1) return new ResultModel(0,"删除失败");
        return new ResultModel();
    }
}
