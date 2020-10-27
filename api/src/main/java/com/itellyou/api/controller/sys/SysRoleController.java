package com.itellyou.api.controller.sys;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.sys.SysRoleService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.Params;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    private final SysRoleService roleService;

    public SysRoleController(SysRoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("")
    public ResultModel list(UserInfoModel userModel, @RequestParam Map args) {
        Params params = new Params(args);
        Integer offset = params.getPageOffset(0);
        Integer limit = params.getPageLimit(20);
        String name = params.get( "name");
        Boolean disabled = params.getBoolean( "disabled", false);
        Long begin = params.getTimestamp("begin");
        Long end = params.getTimestamp("end");
        Long ipLong = params.getIPLong();
        Map<String,String> order = params.getOrderDefault("created_time","desc");
        PageModel<SysRoleModel> data = roleService.page(null,name,disabled,null,userModel.getId(),begin,end,ipLong,order,offset,limit);
        return new ResultModel(data,new Labels.LabelModel(SysRoleModel.class,"info"));
    }

    @GetMapping("/query/name")
    public ResultModel queryName(UserInfoModel userModel,@RequestParam @NotBlank String name){
        SysRoleModel model = roleService.findByName(name,userModel.getId());
        if(model != null){
            return new ResultModel(500,"名称不可用",name);
        }
        return new ResultModel(name);
    }

    @PutMapping("")
    public ResultModel add(UserInfoModel userModel, HttpServletRequest request, @MultiRequestBody @NotBlank String name , @MultiRequestBody(required = false) String description){
        SysRoleModel model = roleService.findByName(name,userModel.getId());
        if(model != null){
            return new ResultModel(500,"名称不可用",name);
        }
        SysRoleModel roleModel = new SysRoleModel();
        roleModel.setName(name);
        roleModel.setSystem(false);
        roleModel.setDisabled(false);
        roleModel.setDescription(description);
        roleModel.setCreatedUserId(userModel.getId());
        roleModel.setCreatedTime(DateUtils.toLocalDateTime());
        roleModel.setCreatedIp(IPUtils.toLong(request));
        int result = roleService.insert(roleModel);
        if(result != 1) return new ResultModel(0,"新增失败");
        return new ResultModel(roleModel);
    }

    @DeleteMapping("")
    public ResultModel remove(UserInfoModel userModel,@RequestParam @NotNull Long id){
        try {
            roleService.delete(id, userModel.getId());
        }catch (Exception e){
            return new ResultModel(0, "删除失败");
        }
        return new ResultModel();
    }

    @PostMapping("")
    public ResultModel update(UserInfoModel userModel,@MultiRequestBody Long id ,
                              @MultiRequestBody(required = false) String name,
                              @MultiRequestBody(required = false) String disabled,
                              @MultiRequestBody(required = false) String description){

        if(StringUtils.isEmpty(name) && StringUtils.isEmpty(disabled) && StringUtils.isEmpty(description)) return new ResultModel(500,"无效的参数");
        SysRoleModel model = roleService.findById(id);
        if(model == null){
            return new ResultModel(500,"不存在的角色",id);
        }
        if(StringUtils.isNotEmpty(name)) {
            model = roleService.findByName(name, userModel.getId());
            if (model != null) {
                return new ResultModel(500, "名称不可用", name);
            }
        }
        roleService.update(id,name,disabled == null ? null : disabled == "true",description);
        return new ResultModel();
    }
}
