package com.itellyou.model.question;

import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class QuestionAnswerOperationalModel extends OperationalModel {

    private Long questionId;
    private Long questionUserId;

    public QuestionAnswerOperationalModel(EntityAction action, EntityType type,Long questionId,Long questionUserId, Long targetId, Long targetUserId, Long createdUserId, LocalDateTime createdTime, Long createdIp){
        setAction(action);
        setType(type);
        setTargetId(targetId);
        setTargetUserId(targetUserId);
        setCreatedUserId(createdUserId);
        setCreatedTime(createdTime);
        setCreatedIp(createdIp);
        setQuestionId(questionId);
        setQuestionUserId(questionUserId);
    }
}
