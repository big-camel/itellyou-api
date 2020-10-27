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
public class NotificationModel extends OperationalBaseModel {
    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private boolean isRead;
    @JSONField(label = "base")
    private boolean isDeleted;
    private Long receiveId;
    @JSONField(label = "base")
    private Integer mergeCount;
    @JSONField(label = "base")
    private LocalDateTime createdTime;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;
    @JSONField(label = "base")
    private LocalDateTime updatedTime;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long updatedIp;

    public NotificationModel(Boolean isRead, Boolean isDeleted, Long receiveId, EntityAction action, EntityType type, Long targetId, Integer mergeCount, LocalDateTime time, Long ip){
        this.isRead = isRead;
        this.isDeleted = isDeleted;
        this.receiveId = receiveId;
        setAction(action);
        setType(type);
        setTargetId(targetId);
        this.mergeCount = mergeCount;
        this.createdTime = time;
        this.createdIp = ip;
        this.updatedTime = time;
        this.updatedIp = ip;
    }
}
