package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityType;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpLongSerializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class UserNotificationModel extends UserOperationalBaseModel {
    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private boolean isRead;
    @JSONField(label = "base")
    private boolean isDeleted;
    private Long receiveId;
    @JSONField(label = "base")
    private Integer mergeCount;
    @JSONField(serializeUsing = TimestampSerializer.class,label = "base")
    private Long createdTime=0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp;
    @JSONField(serializeUsing = TimestampSerializer.class,label = "base")
    private Long updatedTime=0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long updatedIp;

    public UserNotificationModel(Boolean isRead, Boolean isDeleted, Long receiveId, UserOperationalAction action, EntityType type, Long targetId, Integer mergeCount, Long time, Long ip){
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
