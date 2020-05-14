package com.itellyou.model.sys;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.EnumSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JSONDefault(includes = "base")
public class SysPermissionModel implements CacheEntity {

    @JSONField(label = "base")
    private String name;
    @JSONField(serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private SysPermissionPlatform platform;
    @JSONField(label = "base",serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private SysPermissionType type;
    @JSONField(label = "base",serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private SysPermissionMethod method;
    @JSONField(label = "base")
    private String data;
    private String remark;

    @Override
    public String cacheKey() {
        return name;
    }
}
