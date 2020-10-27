package com.itellyou.model.event;

import com.itellyou.model.question.QuestionAnswerOperationalModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;

import java.time.LocalDateTime;

public class AnswerEvent extends OperationalEvent<QuestionAnswerOperationalModel> {
    public AnswerEvent(Object source, EntityAction action , Long questionId ,Long questionUserId , Long targetId, Long targetUserId, Long createdUserId, LocalDateTime createdTime, Long createdIp) {
        super(source);
        QuestionAnswerOperationalModel model = new QuestionAnswerOperationalModel(action, EntityType.ANSWER,questionId,questionUserId,targetId,targetUserId,createdUserId,createdTime,createdIp);
        super.setOperationalModel(model);
    }
}
