package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class UserOperationalDetailModel extends UserOperationalModel {
    @JSONField(label = "base")
    private Object target;

    public UserOperationalDetailModel(UserOperationalModel model) {
        super(model.getId(),model.getTargetUserId(),model.getCreatedUserId(),model.getCreatedTime(),model.getCreatedIp());
        setAction(model.getAction());
        setType(model.getType());
        setTargetId(model.getTargetId());
    }
}
