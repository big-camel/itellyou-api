package com.itellyou.api.controller.tag;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.tag.TagGroupModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.tag.TagGroupService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/tag")
public class TagGroupController {

    private final TagGroupService groupService;

    public TagGroupController(TagGroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/group")
    public ResultModel group(@RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        return new ResultModel(groupService.page(null,null,null,false,true,null,null,null,null,null,offset,limit));
    }

    @PutMapping("/group")
    public ResultModel create(HttpServletRequest request, UserInfoModel userModel,@MultiRequestBody @NotBlank String name){
        TagGroupModel groupModel = groupService.findByName(name);
        if(groupModel != null){
            return new ResultModel(500,"名称已存在");
        }
        groupModel = new TagGroupModel();
        groupModel.setName(name);
        groupModel.setCreatedUserId(userModel.getId());
        groupModel.setCreatedTime(DateUtils.getTimestamp());
        groupModel.setCreatedIp(IPUtils.toLong(request));
        int result = groupService.insert(groupModel);
        if(result != 1) return new ResultModel(0,"创建失败");
        return new ResultModel();
    }

    @PostMapping("/group")
    public ResultModel update(@MultiRequestBody @NotNull Long id, @MultiRequestBody @NotBlank String name){
        TagGroupModel groupModel = groupService.findByName(name);
        if(groupModel != null){
            return new ResultModel(500,"名称已存在");
        }
        int result = groupService.updateNameById(id,name);
        if(result != 1) return new ResultModel(0,"更新失败");
        return new ResultModel();
    }

    @DeleteMapping("/group")
    public ResultModel delete(@RequestParam @NotNull Long id){
        int result = groupService.deleteById(id);
        if(result != 1) return new ResultModel(0,"删除失败");
        return new ResultModel();
    }
}
