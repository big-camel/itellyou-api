package com.itellyou.api.controller.user;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserRoleModel;
import com.itellyou.service.sys.SysRoleService;
import com.itellyou.service.user.access.UserRoleService;
import com.itellyou.service.user.UserSingleService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/user/role")
public class RoleController {

    private final UserSingleService searchService;
    private final UserRoleService userRoleService;
    private final SysRoleService roleService;

    public RoleController(UserSingleService searchService, UserRoleService userRoleService, SysRoleService roleService) {
        this.searchService = searchService;
        this.userRoleService = userRoleService;
        this.roleService = roleService;
    }

    @GetMapping("")
    public ResultModel role(UserInfoModel userModel, @RequestParam(name = "user_id") @NotNull Long userId) {
        Map<String,String> order = new HashMap<>();
        order.put("name","asc");
        List<SysRoleModel> roleModels = roleService.search(null,null,null,false,userModel.getId(),null,null,null,null,null,null);
        List<UserRoleModel> rankRoleModels = userRoleService.search(null,userId,null,null,null,null,null,null);
        List<Map<String,Object>> data = new ArrayList<>();
        for (SysRoleModel roleModel : roleModels){
            boolean isChecked = false;
            Map<String,Object> mapData = new HashMap<>();
            for (UserRoleModel userRoleModel : rankRoleModels){
                if(roleModel.getId().equals(userRoleModel.getRoleId())){
                    isChecked = true;
                    break;
                }
            }
            mapData.put("checked",isChecked);
            mapData.put("role",roleModel);
            data.add(mapData);
        }
        return new ResultModel(data,new Labels.LabelModel(SysRoleModel.class,"info"));
    }

    @DeleteMapping()
    public ResultModel remove(UserInfoModel userModel,@RequestParam(name = "role_id") @NotNull Long roleId, @RequestParam(name = "user_id") @NotNull Long userId){
        SysRoleModel roleModel = roleService.findById(roleId);
        if(roleModel == null || !roleModel.getCreatedUserId().equals(userModel.getId())) return new ResultModel(500,"错误的角色编号");
        int result = userRoleService.delete(userId,roleId);
        if(result != 1) return new ResultModel(500,"删除失败");
        return new ResultModel();
    }

    @PutMapping("")
    public ResultModel add(HttpServletRequest request, UserInfoModel userModel,
                           @MultiRequestBody(value = "role_id") @NotNull Long roleId,
                           @MultiRequestBody(value = "user_id") @NotNull Long userId){
        SysRoleModel roleModel = roleService.findById(roleId);
        if(roleModel == null || !roleModel.getCreatedUserId().equals(userModel.getId())) return new ResultModel(500,"错误的角色编号");
        UserInfoModel infoModel = searchService.findById(userId);
        if(infoModel == null) return new ResultModel(500,"错误的用户编号");
        UserRoleModel rankRoleModel = new UserRoleModel(userId,roleId, DateUtils.getTimestamp(),userModel.getId(), IPUtils.toLong(request));
        int result = userRoleService.insert(rankRoleModel);
        if(result != 1) return new ResultModel(500,"新增失败");
        return new ResultModel();
    }
}
