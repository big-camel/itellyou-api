package com.itellyou.model.event;

import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.sys.EntityType;

import java.time.LocalDateTime;

public class ColumnEvent extends OperationalEvent {

    public ColumnEvent(Object source, EntityAction action , Long targetId, Long targetUserId, Long createdUserId, LocalDateTime createdTime, Long createdIp) {
        super(source);
        OperationalModel model = new OperationalModel(action, EntityType.COLUMN,targetId,targetUserId,createdUserId,createdTime,createdIp);
        super.setOperationalModel(model);
    }

    public ColumnEvent(Object source){
        super(source);
    }
}
