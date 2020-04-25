package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityType;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpLongSerializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class UserOperationalModel extends UserOperationalBaseModel {
    @JSONField(label = "base")
    private Long id;
    private Long targetUserId;
    private Long createdUserId=0l;
    @JSONField(serializeUsing = TimestampSerializer.class,label = "base")
    private Long createdTime=0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp;

    public UserOperationalModel(UserOperationalAction action, EntityType type, Long targetId, Long targetUserId, Long createdUserId, Long createdTime, Long createdIp){
        setAction(action);
        setType(type);
        setTargetId(targetId);
        setTargetUserId(targetUserId);
        setCreatedUserId(createdUserId);
        setCreatedTime(createdTime);
        setCreatedIp(createdIp);
    }
}
