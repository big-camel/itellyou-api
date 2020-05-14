package com.itellyou.model.sys;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.serialize.EnumSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysPathModel implements CacheEntity {

    private String path;
    @JSONField(serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private SysPath type;
    private Long id;

    @Override
    public String cacheKey() {
        return new StringBuilder(String.valueOf(id)).append("-").append(type.getValue()).toString();
    }
}
