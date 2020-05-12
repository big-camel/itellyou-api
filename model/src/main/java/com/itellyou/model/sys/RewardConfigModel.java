package com.itellyou.model.sys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardConfigModel implements CacheEntity {
    private String id;
    private RewardType type;
    private Double min;
    private Double max;
    private String unit;

    @Override
    public String cacheKey() {
        return id;
    }
}
