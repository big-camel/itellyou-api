package com.itellyou.model.user;

import com.itellyou.util.CacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBankModel implements CacheEntity {
    private Long userId;
    private Integer credit;
    private Double cash;
    private Integer score;

    @Override
    public String cacheKey() {
        return userId.toString();
    }
}
