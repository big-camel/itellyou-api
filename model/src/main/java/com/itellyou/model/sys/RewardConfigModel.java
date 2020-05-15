package com.itellyou.model.sys;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.serialize.EnumSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardConfigModel implements CacheEntity {
    private String id;
    @JSONField(serializeUsing = EnumSerializer.class,deserializeUsing = EnumSerializer.class)
    private RewardType type;
    private Double min;
    private Double max;
    private String unit;

    @Override
    public String cacheKey() {
        return id;
    }
}
