package com.itellyou.api.controller.user;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserStarDetailModel;
import com.itellyou.model.user.UserStarModel;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.service.user.UserStarService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/user")
public class UserStarController {

    private final UserStarService starService;
    private final UserSearchService userSearchService;

    public UserStarController(UserStarService starService,UserSearchService userSearchService){
        this.starService = starService;
        this.userSearchService = userSearchService;
    }

    @GetMapping("/star")
    public Result star(UserInfoModel userModel, @RequestParam(required = false,name = "user_id") Long userId , @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userId == null && userModel == null) return new Result(401,"未登陆");
        if(userId == null) userId = userModel.getId();
        Long searchId = userModel != null ? userModel.getId() : null;
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        PageModel<UserStarDetailModel> pageData = starService.page(null,userId,searchId,null,null,null,order,offset,limit);
        return new Result(pageData,new Labels.LabelModel(UserInfoModel.class,"base","info"));
    }

    @GetMapping("/follower")
    public Result follower(UserInfoModel userModel, @RequestParam(required = false,name = "user_id") Long userId, @RequestParam(required = false) Integer day, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userId == null && userModel == null) return new Result(401,"未登陆");
        if(userId == null) userId = userModel.getId();
        Long searchId = userModel != null ? userModel.getId() : null;
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        Long beginTime = null;
        if(day != null){
            beginTime = DateUtils.getTimestamp() - day * 86400;
        }
        PageModel<UserStarDetailModel> pageData = starService.page(userId,null,searchId,beginTime,null,null,order,offset,limit);
        return new Result(pageData,new Labels.LabelModel(UserInfoModel.class,"base","info"));
    }

    @PostMapping("/star")
    public Result star(HttpServletRequest request,UserInfoModel userModel, @MultiRequestBody @NotNull Long id){
        if(userModel == null) return new Result(401,"未登陆");
        UserInfoModel infoModel = userSearchService.findById(id);
        if(infoModel == null) return new Result(404,"错误的id");
        if(infoModel.isDisabled() || userModel.isDisabled()) return new Result(0,"错误的用户状态");
        if(id.equals(userModel.getId())) return new Result(0,"不能自己关注自己");
        String clientIp = IPUtils.getClientIp(request);
        Long ip = IPUtils.toLong(clientIp);
        UserStarModel starModel = new UserStarModel(id, DateUtils.getTimestamp(),userModel.getId(),ip);
        try{
            int result = starService.insert(starModel);
            if(result != 1) throw new Exception("关注失败");
            return new Result(infoModel.getFollowerCount() + 1);
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }

    @DeleteMapping("/star")
    public Result delete(UserInfoModel userModel, @MultiRequestBody @NotNull Long id){
        if(userModel == null) return new Result(401,"未登陆");
        if(userModel.isDisabled()) return new Result(0,"错误的用户状态");
        try{
            int result = starService.delete(id,userModel.getId());
            if(result != 1) throw new Exception("取消关注失败");
            UserInfoModel infoModel = userSearchService.findById(id);
            return new Result(infoModel.getFollowerCount());
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }
}
