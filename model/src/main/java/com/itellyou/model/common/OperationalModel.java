package com.itellyou.model.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpDeserializer;
import com.itellyou.util.serialize.IpSerializer;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class OperationalModel extends OperationalBaseModel {
    @JSONField(label = "base")
    private Long id;
    private Long targetUserId;
    private Long createdUserId=0l;
    @JSONField(label = "base")
    private LocalDateTime createdTime;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;

    public OperationalModel(EntityAction action, EntityType type, Long targetId, Long targetUserId, Long createdUserId, LocalDateTime createdTime, Long createdIp){
        setAction(action);
        setType(type);
        setTargetId(targetId);
        setTargetUserId(targetUserId);
        setCreatedUserId(createdUserId);
        setCreatedTime(createdTime);
        setCreatedIp(createdIp);
    }
}
