package com.itellyou.api.controller.tag;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.tag.TagVersionModel;
import com.itellyou.service.tag.TagVersionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/tag/{tagId:\\d+}/version")
public class TagVersionController {

    private final TagVersionService versionService;

    public TagVersionController(TagVersionService versionService){
        this.versionService = versionService;
    }

    @GetMapping("")
    public ResultModel list(@PathVariable @NotNull Long tagId){

        List<TagVersionModel> listVersion = versionService.searchByTagId(tagId);
        return new ResultModel(listVersion);
    }

    @GetMapping("/{versionId:\\d+}")
    public ResultModel find(@PathVariable @NotNull Long versionId, @PathVariable @NotNull Long tagId){
        TagVersionModel versionModel = versionService.findByTagIdAndId(versionId,tagId);
        if(versionModel == null){
            return new ResultModel(0,"错误的编号");
        }
        return new ResultModel(versionModel);
    }

    private String getVersionHtml(TagVersionModel versionModel){
        StringBuilder currentString = new StringBuilder("<!doctype html>");
        currentString.append(versionModel.getHtml());
        return currentString.toString();
    }

    @GetMapping("/{current:\\d+}...{target:\\d+}")
    public ResultModel compare(@PathVariable @NotNull Long current, @PathVariable @NotNull Long target, @PathVariable @NotNull Long tagId){
        TagVersionModel currentVersion = versionService.findByTagIdAndId(current,tagId);
        if(currentVersion == null){
            return new ResultModel(0,"错误的当前编号");
        }

        TagVersionModel targetVersion = versionService.findByTagIdAndId(target,tagId);
        if(targetVersion == null){
            return new ResultModel(0,"错误的目标编号");
        }

        Map<String,String> htmlData = new HashMap<>();
        htmlData.put("current",getVersionHtml(currentVersion));
        htmlData.put("target",getVersionHtml(targetVersion));

        return new ResultModel(htmlData);
    }
}
