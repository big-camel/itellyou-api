package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class UserBankModel implements CacheEntity {
    private Long userId;
    @JSONField(label = "base")
    private Integer credit;
    @JSONField(label = "base")
    private Double cash;
    @JSONField(label = "base")
    private Integer score;

    @Override
    public Long cacheKey() {
        return userId;
    }
}
