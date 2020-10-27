package com.itellyou.api.controller.software;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.software.SoftwareGroupModel;
import com.itellyou.model.software.SoftwareVersionDetailModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.software.SoftwareGroupService;
import com.itellyou.service.software.SoftwareVersionSearchService;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/software/{softwareId:\\d+}/version")
public class SoftwareVersionController {
    private final SoftwareVersionSearchService versionService;
    private final SoftwareGroupService groupService;

    @Autowired
    public SoftwareVersionController(SoftwareVersionSearchService versionService, SoftwareGroupService groupService){
        this.versionService = versionService;
        this.groupService = groupService;
    }

    @GetMapping("")
    public ResultModel list(@PathVariable @NotNull Long softwareId){
        List<SoftwareVersionDetailModel> listVersion = versionService.searchBySoftwareId(softwareId);
        return new ResultModel(listVersion,
                new Labels.LabelModel(UserInfoModel.class,"base"),
                new Labels.LabelModel(TagInfoModel.class,"base"));
    }

    @GetMapping("/{versionId:\\d+}")
    public ResultModel find(@PathVariable @NotNull Long versionId, @PathVariable @NotNull Long softwareId){
        SoftwareVersionDetailModel versionModel = versionService.getDetail(versionId);
        if(versionModel == null || !versionModel.getSoftwareId().equals(softwareId)){
            return new ResultModel(0,"错误的编号");
        }
        return new ResultModel(versionModel,
                new Labels.LabelModel(UserInfoModel.class,"base"),
                new Labels.LabelModel(TagInfoModel.class,"base"));
    }

    private String getVersionHtml(SoftwareVersionDetailModel versionModel){
        StringBuilder currentString = new StringBuilder("<div>");
        currentString.append("<h2>" + versionModel.getName() + "</h2>");
        List<TagDetailModel> currentTagList = versionModel.getTags();
        currentString.append("<p class=\"info-layout\">");
        if(currentTagList != null && currentTagList.size() > 0){
            for(TagDetailModel tagInfo : currentTagList){
                currentString.append("<span>" + tagInfo.getName() + "</span>");
            }
            currentString.append("，");
        }
        currentString.append("</p>");
        Long groupId = versionModel.getGroupId();
        SoftwareGroupModel groupModel = null;
        if(groupId != 0){
            groupModel = groupService.searchById(groupId);
            if(groupModel == null){
                groupModel = new SoftwareGroupModel();
                groupModel.setName("未知分类");
            }
        }
        currentString.append("</div>");
        currentString.append(versionModel.getHtml());

        return currentString.toString();
    }

    @GetMapping("/{current:\\d+}...{target:\\d+}")
    public ResultModel compare(UserInfoModel userModel,@PathVariable @NotNull Long current, @PathVariable @NotNull Long target, @PathVariable @NotNull Long softwareId){
        SoftwareVersionDetailModel currentVersion = versionService.getDetail(current);
        if(currentVersion == null|| !currentVersion.getSoftwareId().equals(softwareId)){
            return new ResultModel(0,"错误的当前编号");
        }

        SoftwareVersionDetailModel targetVersion = versionService.getDetail(target);
        if(targetVersion == null|| !targetVersion.getSoftwareId().equals(softwareId)){
            return new ResultModel(0,"错误的目标编号");
        }

        Map<String,String> htmlData = new HashMap<>();
        htmlData.put("current",getVersionHtml(currentVersion));
        htmlData.put("target",getVersionHtml(targetVersion));

        return new ResultModel(htmlData);
    }
}
