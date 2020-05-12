package com.itellyou.model.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpDeserializer;
import com.itellyou.util.serialize.IpSerializer;
import com.itellyou.util.serialize.TimestampDeserializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class NotificationQueueModel extends OperationalBaseModel {
    @JSONField(label = "base")
    private Long id;
    private Long targetUserId;
    private Long createdUserId=0l;
    @JSONField(serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class,label = "base")
    private Long createdTime=0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;

    public NotificationQueueModel(EntityAction action, EntityType type, Long targetId, Long targetUserId, Long createdUserId, Long createdTime, Long createdIp){
        setAction(action);
        setType(type);
        setTargetId(targetId);
        setTargetUserId(targetUserId);
        setCreatedUserId(createdUserId);
        setCreatedTime(createdTime);
        setCreatedIp(createdIp);
    }
}
