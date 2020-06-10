package com.itellyou.model.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.CacheEntity;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.util.serialize.EnumSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDisplayModel implements CacheEntity {
    private Long userId;
    @JSONField(serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private EntityAction action;
    @JSONField(serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private EntityType type;
    @JSONField(serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private NotificationDisplay value;

    @Override
    public String cacheKey() {
        return userId + "-" + action + "-" + type;
    }
}
