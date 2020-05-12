package com.itellyou.api.controller.user;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserRankModel;
import com.itellyou.model.user.UserRankRoleModel;
import com.itellyou.service.sys.SysRoleService;
import com.itellyou.service.user.UserRankRoleService;
import com.itellyou.service.user.UserRankService;
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
@RequestMapping("/user/rank/role")
public class RankRoleController {

    private final UserRankService rankService;
    private final UserRankRoleService rankRoleService;
    private final SysRoleService roleService;

    public RankRoleController(UserRankService rankService, UserRankRoleService rankRoleService, SysRoleService roleService) {
        this.rankService = rankService;
        this.rankRoleService = rankRoleService;
        this.roleService = roleService;
    }

    @GetMapping("")
    public ResultModel role(UserInfoModel userModel, @RequestParam(name = "rank_id") @NotNull Long rankId) {
        Map<String,String> order = new HashMap<>();
        order.put("name","asc");
        List<SysRoleModel> roleModels = roleService.search(null,null,null,false,userModel.getId(),null,null,null,null,null,null);
        List<UserRankRoleModel> rankRoleModels = rankRoleService.search(null,rankId,null,null,null,null,null,null);
        List<Map<String,Object>> data = new ArrayList<>();
        for (SysRoleModel roleModel : roleModels){
            boolean isChecked = false;
            Map<String,Object> mapData = new HashMap<>();
            for (UserRankRoleModel rankRoleModel : rankRoleModels){
                if(roleModel.getId().equals(rankRoleModel.getRoleId())){
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
    public ResultModel remove(UserInfoModel userModel,@RequestParam(name = "role_id") @NotNull Long roleId, @RequestParam(name = "rank_id") @NotNull Long rankId){
        SysRoleModel roleModel = roleService.findById(roleId);
        if(roleModel == null || !roleModel.getCreatedUserId().equals(userModel.getId())) return new ResultModel(500,"错误的角色编号");
        int result = rankRoleService.delete(rankId,roleId);
        if(result != 1) return new ResultModel(500,"删除失败");
        return new ResultModel();
    }

    @PutMapping("")
    public ResultModel add(HttpServletRequest request, UserInfoModel userModel,
                           @MultiRequestBody(value = "role_id") @NotNull Long roleId,
                           @MultiRequestBody(value = "rank_id") @NotNull Long rankId){
        SysRoleModel roleModel = roleService.findById(roleId);
        if(roleModel == null || !roleModel.getCreatedUserId().equals(userModel.getId())) return new ResultModel(500,"错误的角色编号");
        UserRankModel rankModel = rankService.findById(rankId);
        if(rankModel == null) return new ResultModel(500,"错误的等级编号");
        UserRankRoleModel rankRoleModel = new UserRankRoleModel(rankId,roleId, DateUtils.getTimestamp(),userModel.getId(), IPUtils.toLong(request));
        int result = rankRoleService.insert(rankRoleModel);
        if(result != 1) return new ResultModel(500,"新增失败");
        return new ResultModel();
    }
}
