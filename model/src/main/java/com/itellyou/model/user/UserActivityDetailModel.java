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
public class UserActivityDetailModel extends UserActivityModel {
    @JSONField(label = "base")
    private Object target;

    public UserActivityDetailModel(UserActivityModel model,Object target){
        super(model.getAction(),model.getType(),model.getTargetId(),model.getTargetUserId(),model.getCreatedUserId(),model.getCreatedTime(),model.getCreatedIp());
        setTarget(target);
    }
}
