package com.itellyou.api.controller;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.article.ArticleCommentDetailModel;
import com.itellyou.model.question.QuestionAnswerCommentDetailModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionCommentDetailModel;
import com.itellyou.model.user.*;
import com.itellyou.service.user.UserNotificationDisplayService;
import com.itellyou.service.user.UserNotificationService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.annotation.MultiRequestBody;
import com.itellyou.util.serialize.filter.Labels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/notifications")
public class NotificationsController {

    private final UserNotificationService notificationService;
    private final UserNotificationDisplayService displayService;

    @Autowired
    public NotificationsController(UserNotificationService notificationService,UserNotificationDisplayService displayService){
        this.notificationService = notificationService;
        this.displayService = displayService;
    }

    @GetMapping("")
    public Result list(UserInfoModel userModel,@RequestParam(required = false) String action,@RequestParam(required = false) String type,@RequestParam(required = false,name = "is_read") Boolean isRead, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new Result(401,"未登陆");
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        UserOperationalAction notificationAction = null;
        EntityType notificationType = null;
        try {
            notificationAction = UserOperationalAction.valueOf(action.toUpperCase());
            notificationType = EntityType.valueOf(type.toUpperCase());
        }catch (Exception e){}
        PageModel<UserNotificationDetailModel> data = notificationService.page(notificationAction,notificationType,5,false,isRead,userModel.getId(),null,null,null,order,offset,limit);
        return new Result(data ,
                new Labels.LabelModel(QuestionAnswerDetailModel.class,"base","question"),
                new Labels.LabelModel(QuestionCommentDetailModel.class,"base","question"),
                new Labels.LabelModel(QuestionAnswerCommentDetailModel.class,"base","answer"),
                new Labels.LabelModel(ArticleCommentDetailModel.class,"base","article")
        );
    }

    @GetMapping("/count")
    public Result count(UserInfoModel userModel){
        if(userModel == null) return new Result(401,"未登陆");
        int count = notificationService.count(null,null,null,false,false,userModel.getId(),null,null,null);
        return new Result(count);
    }

    @PutMapping("")
    public Result read(UserInfoModel userModel, HttpServletRequest request){
        if(userModel == null) return new Result(401,"未登陆");
        String ip = IPUtils.getClientIp(request);
        notificationService.updateIsReadByReceiveId(userModel.getId(),true, DateUtils.getTimestamp(),IPUtils.toLong(ip));
        return new Result();
    }

    @DeleteMapping("")
    public Result delete(UserInfoModel userModel, HttpServletRequest request, @MultiRequestBody Long id){
        if(userModel == null) return new Result(401,"未登陆");
        String ip = IPUtils.getClientIp(request);
        int result = notificationService.updateIsDeletedByIdAndReceiveId(id,userModel.getId(),true, DateUtils.getTimestamp(),IPUtils.toLong(ip));
        if(result != 1) return new Result(0,"删除失败");
        return new Result();
    }

    @GetMapping("/settings")
    public Result getSettings(UserInfoModel userModel){
        if(userModel == null) return new Result(401,"未登陆");
        List<UserNotificationDisplayModel> list = displayService.searchByDefault(userModel.getId(),null,null);
        return new Result(list);
    }

    @PutMapping("/settings")
    public Result setSettings(UserInfoModel userModel,@MultiRequestBody String action,@MultiRequestBody String type,@MultiRequestBody String value) {
        if(userModel == null) return new Result(401,"未登陆");
        try{
            UserOperationalAction notificationAction = UserOperationalAction.valueOf(action.toUpperCase());
            EntityType notificationType = EntityType.valueOf(type.toUpperCase());
            UserNotificationDisplay notificationDisplay = UserNotificationDisplay.valueOf(value.toUpperCase());
            int result = displayService.insertOrUpdate(new UserNotificationDisplayModel(userModel.getId(),notificationAction,notificationType,notificationDisplay));
            if(result < 1) return new Result(0,"设置错误");
            return new Result();
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }
}
