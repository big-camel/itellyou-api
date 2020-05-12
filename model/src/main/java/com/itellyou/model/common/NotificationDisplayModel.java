package com.itellyou.model.common;

import com.itellyou.model.sys.CacheEntity;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.EntityAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDisplayModel implements CacheEntity {
    private Long userId;
    private EntityAction action;
    private EntityType type;
    private NotificationDisplay value;

    @Override
    public String cacheKey() {
        return userId + "-" + action + "-" + type;
    }
}
