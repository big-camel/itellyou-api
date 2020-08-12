package com.itellyou.api.controller.software;

import com.itellyou.model.common.ResultModel;
import com.itellyou.service.software.SoftwareGroupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/software/group")
public class SoftwareGroupController {

    private final SoftwareGroupService groupService;

    public SoftwareGroupController(SoftwareGroupService groupService) {
        this.groupService = groupService;
    }

    @RequestMapping("/list")
    public ResultModel list(@RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        return new ResultModel(groupService.page(null,null,null,null,null,null,null,offset,limit));
    }
}
