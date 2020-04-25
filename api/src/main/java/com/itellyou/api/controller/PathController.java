package com.itellyou.api.controller;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.sys.SysPathModel;
import com.itellyou.service.sys.SysPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@Validated
@RestController
@RequestMapping("/path")
public class PathController {

    private final SysPathService pathService;

    @Autowired
    public PathController(SysPathService pathService){
        this.pathService = pathService;
    }

    @GetMapping("/find")
    public Result query(@RequestParam @NotBlank String path){
        path = path.toLowerCase();
        SysPathModel pathModel = pathService.findByPath(path);
        if(pathModel != null){
            return new Result(pathModel);
        }
        return new Result(404,"Not Found",path);
    }
}
