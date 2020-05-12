package com.itellyou.model.event;

import com.itellyou.model.common.NotificationGroupCountModel;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class NotificationEvent extends ApplicationEvent {

    private List<NotificationGroupCountModel> groupCountModels;

    public List<NotificationGroupCountModel> getGroupCountModels(){
        return this.groupCountModels;
    }

    public void setGroupCountModels(List<NotificationGroupCountModel> groupCountModels){
        this.groupCountModels = groupCountModels;
    }

    private Long userId;

    public Long getUserId(){return this.userId;}

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public NotificationEvent(Object source,Long userId, List<NotificationGroupCountModel> groupCountModels) {
        super(source);
        setUserId(userId);
        setGroupCountModels(groupCountModels);
    }
}
