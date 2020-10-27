package com.itellyou.api.controller.tag;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.tag.TagInfoService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.service.tag.TagSingleService;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.util.Params;
import com.itellyou.util.StringUtils;
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

    private final TagSearchService searchService;
    private final UserDraftService draftService;
    private final TagInfoService tagService;
    private final TagSingleService tagSingleService;

    @Autowired
    public TagController(TagSearchService searchService, UserDraftService draftService, TagInfoService tagService, TagSingleService tagSingleService){
        this.searchService = searchService;
        this.draftService = draftService;
        this.tagService = tagService;
        this.tagSingleService = tagSingleService;
    }

    @GetMapping("/search")
    public ResultModel search(@RequestParam("w") @NotBlank String word){
        return new ResultModel(searchService.search(word,0,10));
    }

    @GetMapping("/list")
    public ResultModel list(UserInfoModel userModel, @RequestParam(required = false) String name,
                            @RequestParam(required = false) String disabled,
                            @RequestParam(required = false , name = "group_id") Long groupId,
                            @RequestParam(required = false,defaultValue = "") String type,
                            @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        Long searchUserId = userModel == null ? null : userModel.getId();
        Boolean isDisabled = false;
        if(disabled != null && disabled.equals("all")){
            isDisabled = null;
        }else if(disabled != null && disabled.equals("true")){
            isDisabled = true;
        }
        Map<String,String > order;
        switch (type) {
            case "hot":
                order = new HashMap<>();
                order.put("question_count", "desc");
                order.put("article_count", "desc");
                order.put("star_count", "desc");
                return new ResultModel(searchService.page(name,null,groupId,null,searchUserId,true,isDisabled,true,null,null,null,null,null,null,null,null,null,order,offset,limit));
            default:
                return new ResultModel(searchService.page(name,null,groupId,null,searchUserId,true,isDisabled,true,null,null,null,null,null,null,null,null,null,null,offset,limit));
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
        TagInfoModel tagModel = tagSingleService.findByName(name);
        if(tagModel == null) return new ResultModel(0,"无记录");
        return new ResultModel(tagModel);
    }

    @GetMapping("/{id:\\d+}/user_draft")
    public ResultModel find(UserInfoModel userModel, @PathVariable Long id){
        if(userModel == null){
            return new ResultModel(401,"未登陆");
        }

        TagInfoModel infoModel = tagSingleService.findById(id);
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

    @PostMapping("/{id:\\d+}")
    public ResultModel update(@PathVariable Long id, @RequestBody Map args){
        Params params = new Params(args);
        String name = params.get("name");
        if(StringUtils.isNotEmpty(name)){
            TagInfoModel tagModel = tagSingleService.findByName(name);
            if(tagModel != null) {
                return new ResultModel(500,"名称已存在");
            }
        }
        Long groupId = params.get("group_id",Long.class);
        Boolean isDisabled = params.get("disabled",Boolean.class);
        int result = tagService.updateById(id,name,groupId,isDisabled);
        if(result != 1) return new ResultModel(500,"更新失败");
        return new ResultModel();
    }
}
