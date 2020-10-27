package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.common.OperationalBaseModel;
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
public class UserActivityModel extends OperationalBaseModel {
    private Long targetUserId;
    private Long createdUserId=0l;
    @JSONField(label = "base")
    private LocalDateTime createdTime;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;

    public UserActivityModel(EntityAction action, EntityType type,Long targetId,Long targetUserId,Long createdUserId,LocalDateTime createdTime,Long createdIp){
        super(action,type,targetId);
        setTargetUserId(targetUserId);
        setCreatedUserId(createdUserId);
        setCreatedTime(createdTime);
        setCreatedIp(createdIp);
    }
}
