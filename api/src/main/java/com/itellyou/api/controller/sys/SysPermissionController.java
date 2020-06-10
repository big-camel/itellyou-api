package com.itellyou.api.controller.sys;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.*;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.sys.SysPermissionService;
import com.itellyou.service.user.access.UserRoleService;
import com.itellyou.util.Params;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/system/permission")
public class SysPermissionController {

    private final SysPermissionService permissionService;
    private final UserRoleService userRoleService;

    public SysPermissionController(SysPermissionService permissionService, UserRoleService userRoleService) {
        this.permissionService = permissionService;
        this.userRoleService = userRoleService;
    }

    @GetMapping("")
    public ResultModel list(UserInfoModel userModel, @RequestParam Map params){
        Integer offset = Params.getOrDefault(params,"offset",Integer.class,0);
        Integer limit = Params.getOrDefault(params,"limit",Integer.class,0);
        String name = Params.getOrDefault(params,"name",String.class,null);
        String platformString = Params.getOrDefault(params,"platform",String.class,null);
        String typeString = Params.getOrDefault(params,"type",String.class,null);
        String methodString = Params.getOrDefault(params,"method",String.class,null);

        SysPermissionPlatform platform = null;
        SysPermissionType type = null;
        SysPermissionMethod method = null;
        try {
            platform = StringUtils.isNotEmpty(platformString) ? SysPermissionPlatform.valueOf(platformString.toUpperCase()) : null;
            type = StringUtils.isNotEmpty(typeString) ? SysPermissionType.valueOf(typeString.toUpperCase()): null;
            method = StringUtils.isNotEmpty(methodString) ? SysPermissionMethod.valueOf(methodString.toUpperCase()) : null;
        }catch (Exception e){}

        Map<String,String> order = new HashMap<>();
        order.put("name","asc");
        // 拥有root角色加载全部权限
        List<SysRoleModel> listRole = userRoleService.findRoleByUserId(userModel.getId());
        for (SysRoleModel role : listRole){
            if(role.getId().equals(3l)){
                userModel.setId(null);
            }
        }
        return new ResultModel(permissionService.page(userModel.getId(),platform,type,method,name,order,offset,limit),new Labels.LabelModel(SysPermissionModel.class,"*"));
    }

    @GetMapping("/query/name")
    public ResultModel queryName(@RequestParam @NotBlank String name){
        SysPermissionModel model = permissionService.findByName(name);
        if(model != null){
            return new ResultModel(500,"名称不可用",name);
        }
        return new ResultModel(name);
    }

    @PutMapping("")
    public ResultModel add(@MultiRequestBody @NotBlank String name ,
                           @MultiRequestBody("platform") @NotBlank String platformString,
                           @MultiRequestBody("type") @NotBlank String typeString,
                           @MultiRequestBody("method")@NotBlank String methodString,
                           @MultiRequestBody(required = false) String data,@MultiRequestBody(required = false) String remark){

        SysPermissionPlatform platform;
        SysPermissionType type;
        SysPermissionMethod method;
        try {
            platform = SysPermissionPlatform.valueOf(platformString.toUpperCase());
            type = SysPermissionType.valueOf(typeString.toUpperCase());
            method = SysPermissionMethod.valueOf(methodString.toUpperCase());
        }catch (Exception e){
            return new ResultModel(500,e.getMessage());
        }
        SysPermissionModel model = permissionService.findByName(name);
        if(model != null){
            return new ResultModel(500,"名称不可用",name);
        }
        SysPermissionModel permissionModel = new SysPermissionModel();
        permissionModel.setName(name);
        permissionModel.setPlatform(platform);
        permissionModel.setType(type);
        permissionModel.setMethod(method);
        permissionModel.setData(data);
        permissionModel.setRemark(remark);
        int result = permissionService.insert(permissionModel);
        if(result != 1) return new ResultModel(0,"新增失败");
        return new ResultModel(permissionModel);
    }

    @DeleteMapping("")
    public ResultModel remove(@RequestParam @NotBlank String name){
        int result = permissionService.delete(name);
        if(result != 1) return new ResultModel(0,"删除失败");
        return new ResultModel();
    }

    @PostMapping("")
    public ResultModel update(@MultiRequestBody @NotBlank String name ,
                              @MultiRequestBody("platform") @NotBlank String platformString,
                              @MultiRequestBody("type") @NotBlank String typeString,
                              @MultiRequestBody("method")@NotBlank String methodString,
                              @MultiRequestBody(required = false) String data,@MultiRequestBody(required = false) String remark){
        SysPermissionPlatform platform;
        SysPermissionType type;
        SysPermissionMethod method;
        try {
            platform = SysPermissionPlatform.valueOf(platformString.toUpperCase());
            type = SysPermissionType.valueOf(typeString.toUpperCase());
            method = SysPermissionMethod.valueOf(methodString.toUpperCase());
        }catch (Exception e){
            return new ResultModel(500,e.getMessage());
        }
        SysPermissionModel model = permissionService.findByName(name);
        if(model == null){
            return new ResultModel(500,"不存在的权限",name);
        }
        SysPermissionModel permissionModel = new SysPermissionModel();
        permissionModel.setName(name);
        permissionModel.setPlatform(platform);
        permissionModel.setType(type);
        permissionModel.setMethod(method);
        permissionModel.setData(data);
        permissionModel.setRemark(remark);
        permissionService.updateByName(permissionModel);
        return new ResultModel();
    }
}
