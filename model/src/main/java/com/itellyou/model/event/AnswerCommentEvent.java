package com.itellyou.model.event;

import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.sys.EntityType;

public class AnswerCommentEvent extends OperationalEvent {

    public AnswerCommentEvent(Object source, EntityAction action , Long targetId, Long targetUserId, Long createdUserId, Long createdTime, Long createdIp) {
        super(source);
        OperationalModel model = new OperationalModel(action, EntityType.ANSWER_COMMENT,targetId,targetUserId,createdUserId,createdTime,createdIp);
        super.setOperationalModel(model);
    }
}
