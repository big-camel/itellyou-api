package com.itellyou.api.controller.user;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.sys.SysRolePermissionService;
import com.itellyou.service.user.UserRoleService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.Params;
import com.itellyou.util.serialize.filter.Labels;
import com.itellyou.util.serialize.filter.PrivacyFilter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
    public ResultModel list(UserInfoModel userModel, @RequestParam Map params){
        Integer offset = Params.getOrDefault(params,"offset",Integer.class,0);
        Integer limit = Params.getOrDefault(params,"limit",Integer.class,0);
        String name = Params.getOrDefault(params,"name",String.class,null);
        String mobile = Params.getOrDefault(params,"mobile",String.class,null);
        String email = Params.getOrDefault(params,"email",String.class,null);
        String beginTime = Params.getOrDefault(params,"begin",String.class,null);
        Long begin = DateUtils.getTimestamp(beginTime);
        String endTime = Params.getOrDefault(params,"end",String.class,null);
        Long end = DateUtils.getTimestamp(endTime);
        String ip = Params.getOrDefault(params,"ip",String.class,null);
        Long ipLong = IPUtils.toLong(ip,null);
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<UserDetailModel> pageData = searchService.page(null,null,null,name,mobile,email,begin,end,ipLong,order,offset,limit);
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
