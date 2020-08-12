package com.itellyou.api.controller.software;

import com.itellyou.api.handler.TokenAccessDeniedException;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.software.SoftwareInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.software.SoftwareFileService;
import com.itellyou.service.software.SoftwareSingleService;
import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/software/{softwareId:\\d+}/file")
public class SoftwareFileController {

    private final SoftwareFileService softwareFileService;
    private final SoftwareSingleService softwareSingleService;

    public SoftwareFileController(SoftwareFileService softwareFileService, SoftwareSingleService softwareSingleService) {
        this.softwareFileService = softwareFileService;
        this.softwareSingleService = softwareSingleService;
    }

    @PostMapping("/{fileId:\\d+}/recommend")
    public ResultModel recommend(UserInfoModel userModel, @PathVariable @NotNull Long softwareId, @PathVariable @NotNull Long fileId, @MultiRequestBody int recommend){
        SoftwareInfoModel infoModel = softwareSingleService.findById(softwareId);
        if(infoModel == null) return new ResultModel(404,"Not Found");
        if(!infoModel.getCreatedUserId().equals(userModel.getId())) throw new TokenAccessDeniedException();
        int result = softwareFileService.updateRecommendById(recommend == 1 , fileId);
        if(result == 1) return new ResultModel();
        return new ResultModel(500,"更新失败");
    }
}
