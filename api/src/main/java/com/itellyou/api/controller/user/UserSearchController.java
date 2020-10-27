package com.itellyou.api.controller.user;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.sys.SysRolePermissionService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.service.user.access.UserRoleService;
import com.itellyou.util.Params;
import com.itellyou.util.serialize.filter.Labels;
import com.itellyou.util.serialize.filter.PrivacyFilter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/user")
public class UserSearchController {

    private final UserSearchService searchService;
    private final SysRolePermissionService rolePermissionService;
    private final UserRoleService roleService;

    public UserSearchController(UserSearchService searchService, SysRolePermissionService rolePermissionService, UserRoleService roleService) {
        this.searchService = searchService;
        this.rolePermissionService = rolePermissionService;
        this.roleService = roleService;
    }

    @GetMapping("/list")
    public ResultModel list(UserInfoModel userModel, @RequestParam Map args){
        Params params = new Params(args);
        Integer offset = params.getPageOffset(0);
        Integer limit = params.getPageLimit(20);
        String name = params.get("name");
        String mobile = params.get("mobile");
        String email = params.get("email");
        Long begin = params.getTimestamp("begin");
        Long end = params.getTimestamp("end");
        Long ipLong = params.getIPLong();
        Map<String,String> orderMap = params.getOrderDefault("created_time","desc","created_time");
        PageModel<UserDetailModel> pageData = searchService.page(null,null,null,name,mobile,email,begin,end,ipLong,orderMap,offset,limit);
        ResultModel resultModel = new ResultModel(pageData,new Labels.LabelModel(UserDetailModel.class,"base","info","account","time","bank","rank"));
        // 判断当前用户是否有可以查看用户私密信息的权限
        List<SysRoleModel> userRoles = roleService.findRoleByUserId(userModel.getId());
        List<SysRoleModel> sysRoles = rolePermissionService.findRoleByName("api_user_privacy");
        for (SysRoleModel sysRole : sysRoles){
            if(userRoles.contains(sysRole)){
                return resultModel;
            }
        }
        return resultModel.addFilters(new PrivacyFilter());
    }
}
