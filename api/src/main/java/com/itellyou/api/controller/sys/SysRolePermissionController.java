package com.itellyou.api.controller.sys;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.*;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.sys.SysPermissionService;
import com.itellyou.service.sys.SysRolePermissionService;
import com.itellyou.service.sys.SysRoleService;
import com.itellyou.service.user.access.UserRoleService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.Params;
import com.itellyou.util.annotation.MultiRequestBody;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/system/role/permission")
public class SysRolePermissionController {

    private final SysPermissionService permissionService;
    private final SysRolePermissionService rolePermissionService;
    private final SysRoleService roleService;
    private final UserRoleService userRoleService;

    public SysRolePermissionController(SysPermissionService permissionService, SysRolePermissionService rolePermissionService, SysRoleService roleService, UserRoleService userRoleService) {
        this.permissionService = permissionService;
        this.rolePermissionService = rolePermissionService;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
    }

    @GetMapping("")
    public ResultModel permission(UserInfoModel userModel, @RequestParam(name = "role_id") @NotNull Long roleId,@RequestParam Map args) {
        Params params = new Params(args);
        Integer offset = params.getPageOffset(0);
        Integer limit = params.getPageLimit(20);
        String name = params.get( "name");
        SysPermissionPlatform platform = params.get("platform",SysPermissionPlatform.class);
        SysPermissionType type = params.get("type",SysPermissionType.class);
        SysPermissionMethod method = params.get("method",SysPermissionMethod.class);
        Map<String,String> order = params.getOrderDefault("name","asc","name");

        // 拥有root角色加载全部权限
        List<SysRoleModel> listRole = userRoleService.findRoleByUserId(userModel.getId());
        for (SysRoleModel role : listRole){
            if(role.getId().equals(3l)){
                userModel.setId(null);
            }
        }
        PageModel<SysPermissionModel> pageModel = permissionService.page(userModel.getId(),platform,type,method,name,order,offset,limit);
        List<SysRolePermissionModel> rolePermissionModels = rolePermissionService.findByRoleId(roleId);

        return new ResultModel(pageModel,new Labels.LabelModel(SysPermissionModel.class,"*")).
                extend("checked_keys",rolePermissionModels.stream().map(SysRolePermissionModel::getPermissionName).collect(Collectors.toList()));
    }

    @DeleteMapping()
    public ResultModel remove(UserInfoModel userModel, @RequestParam(name = "role_id") @NotNull Long roleId, @RequestParam(name = "permission_name") @NotBlank String permissionName){
        int result = rolePermissionService.delete(userModel.getId(),roleId,permissionName);
        if(result != 1) return new ResultModel(500,"删除失败");
        return new ResultModel();
    }

    @PutMapping("")
    public ResultModel add(HttpServletRequest request, UserInfoModel userModel,
                           @MultiRequestBody(value = "role_id") @NotNull Long roleId,
                           @MultiRequestBody(value = "permission_name") @NotBlank String permissionName){
        SysRoleModel roleModel = roleService.findById(roleId);
        if(roleModel == null || !roleModel.getCreatedUserId().equals(userModel.getId())) return new ResultModel(500,"错误的角色编号");
        SysRolePermissionModel rolePermissionModel = new SysRolePermissionModel(roleId,permissionName, DateUtils.toLocalDateTime(),userModel.getId(), IPUtils.toLong(request));
        int result = rolePermissionService.insert(rolePermissionModel);
        if(result != 1) return new ResultModel(500,"新增失败");
        return new ResultModel();
    }
}
