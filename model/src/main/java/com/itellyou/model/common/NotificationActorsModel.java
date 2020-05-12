package com.itellyou.model.common;

import com.itellyou.model.sys.CacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationActorsModel implements CacheEntity {
    private Long notificationId;
    private Long userId;
    private Long targetId;

    @Override
    public String cacheKey() {
        return notificationId.toString() + "-" + userId.toString() + "-" + targetId.toString();
    }
}
