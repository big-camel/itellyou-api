package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JSONDefault(includes = "base")
public class UserWithdrawConfigModel implements CacheEntity {
    private String id;
    @JSONField(label = "base")
    private double min;
    @JSONField(label = "base")
    private double max;
    @JSONField(label = "base")
    private double rate;
    private double auto;

    @Override
    public String cacheKey() {
        return id;
    }
}
