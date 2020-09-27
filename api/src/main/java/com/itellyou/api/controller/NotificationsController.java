package com.itellyou.api.controller;

import com.itellyou.model.article.ArticleCommentDetailModel;
import com.itellyou.model.common.NotificationDetailModel;
import com.itellyou.model.common.NotificationDisplay;
import com.itellyou.model.common.NotificationDisplayModel;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.question.QuestionAnswerCommentDetailModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionCommentDetailModel;
import com.itellyou.model.software.SoftwareCommentDetailModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.common.NotificationDisplayService;
import com.itellyou.service.common.NotificationService;
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

    private final NotificationService notificationService;
    private final NotificationDisplayService displayService;

    @Autowired
    public NotificationsController(NotificationService notificationService, NotificationDisplayService displayService){
        this.notificationService = notificationService;
        this.displayService = displayService;
    }

    @GetMapping("")
    public ResultModel list(UserInfoModel userModel, @RequestParam(required = false) String action, @RequestParam(required = false) String type, @RequestParam(required = false,name = "is_read") Boolean isRead, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(userModel == null) return new ResultModel(401,"未登陆");
        Map<String,String> order = new HashMap<>();
        order.put("created_time","desc");
        EntityAction notificationAction = null;
        EntityType notificationType = null;
        try {
            notificationAction = EntityAction.valueOf(action.toUpperCase());
            notificationAction = notificationAction.equals(EntityAction.DEFAULT) ? null : notificationAction;
            notificationType = EntityType.valueOf(type.toUpperCase());
            notificationType = notificationType.equals(EntityType.DEFAULT) ? null : notificationType;
        }catch (Exception e){}
        PageModel<NotificationDetailModel> data = notificationService.page(notificationAction,notificationType,5,false,isRead,userModel.getId(),null,null,null,order,offset,limit);
        return new ResultModel(data ,
                new Labels.LabelModel(QuestionAnswerDetailModel.class,"base","question"),
                new Labels.LabelModel(QuestionCommentDetailModel.class,"base","question"),
                new Labels.LabelModel(QuestionAnswerCommentDetailModel.class,"base","answer"),
                new Labels.LabelModel(ArticleCommentDetailModel.class,"base","article"),
                new Labels.LabelModel(SoftwareCommentDetailModel.class,"base","software")
        );
    }

    @GetMapping("/count")
    public ResultModel count(UserInfoModel userModel){
        if(userModel == null) return new ResultModel(401,"未登陆");
        int count = notificationService.count(null,null,null,false,false,userModel.getId(),null,null,null);
        return new ResultModel(count);
    }

    @PutMapping("")
    public ResultModel read(UserInfoModel userModel, HttpServletRequest request){
        if(userModel == null) return new ResultModel(401,"未登陆");
        String ip = IPUtils.getClientIp(request);
        notificationService.updateIsReadByReceiveId(userModel.getId(),true, DateUtils.getTimestamp(),IPUtils.toLong(ip));
        return new ResultModel();
    }

    @DeleteMapping("")
    public ResultModel delete(UserInfoModel userModel, HttpServletRequest request, @MultiRequestBody Long id){
        if(userModel == null) return new ResultModel(401,"未登陆");
        String ip = IPUtils.getClientIp(request);
        int result = notificationService.updateIsDeletedByIdAndReceiveId(id,userModel.getId(),true, DateUtils.getTimestamp(),IPUtils.toLong(ip));
        if(result != 1) return new ResultModel(0,"删除失败");
        return new ResultModel();
    }

    @GetMapping("/settings")
    public ResultModel getSettings(UserInfoModel userModel){
        if(userModel == null) return new ResultModel(401,"未登陆");
        List<NotificationDisplayModel> list = displayService.searchByDefault(userModel.getId(),null,null);
        return new ResultModel(list);
    }

    @PutMapping("/settings")
    public ResultModel setSettings(UserInfoModel userModel, @MultiRequestBody String action, @MultiRequestBody String type, @MultiRequestBody String value) {
        if(userModel == null) return new ResultModel(401,"未登陆");
        try{
            EntityAction notificationAction = EntityAction.valueOf(action.toUpperCase());
            EntityType notificationType = EntityType.valueOf(type.toUpperCase());
            NotificationDisplay notificationDisplay = NotificationDisplay.valueOf(value.toUpperCase());
            int result = displayService.insertOrUpdate(new NotificationDisplayModel(userModel.getId(),notificationAction,notificationType,notificationDisplay));
            if(result < 1) return new ResultModel(0,"设置错误");
            return new ResultModel();
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }
}
