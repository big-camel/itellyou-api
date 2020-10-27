package com.itellyou.model.common;

import com.itellyou.util.CacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMarkModel implements CacheEntity {
    private Long userId;

    private LocalDateTime updatedTime;

    @Override
    public Long cacheKey() {
        return userId;
    }
}
