package com.itellyou.model.statistics;

import com.itellyou.util.CacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsIncomeTotalModel implements CacheEntity<Long> {
    private Long userId;
    private Double totalAmount;
    private Double tipAmount;
    private Double rewardAmount;
    private Double sharingAmount;
    private Double sellAmount;
    private Double otherAmount;

    @Override
    public Long cacheKey() {
        return userId;
    }
}
