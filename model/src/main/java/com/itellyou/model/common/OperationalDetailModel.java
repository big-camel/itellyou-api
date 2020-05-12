package com.itellyou.model.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class OperationalDetailModel extends OperationalModel {
    @JSONField(label = "base")
    private Object target;

    public OperationalDetailModel(OperationalModel model) {
        super(model.getId(),model.getTargetUserId(),model.getCreatedUserId(),model.getCreatedTime(),model.getCreatedIp());
        setAction(model.getAction());
        setType(model.getType());
        setTargetId(model.getTargetId());
    }
}
