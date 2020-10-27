package com.itellyou.model.sys;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpDeserializer;
import com.itellyou.util.serialize.IpSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class SysAdModel implements CacheEntity<Long> {
    @JSONField( label = "base")
    private Long id;
    @JSONField( label = "base")
    private String name;
    @JSONField( label = "base")
    private AdType type;
    @JSONField( label = "base")
    private String dataId;
    @JSONField( label = "base")
    private Boolean enabledForeign;
    @JSONField( label = "base")
    private Boolean enabledCn;
    @JSONField( label = "base")
    private LocalDateTime createdTime;
    private Long createdUserId = 0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp = 0l;
    @JSONField( label = "base")
    private LocalDateTime updatedTime;
    private Long updatedUserId = 0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long updatedIp = 0l;

    @Override
    public Long cacheKey() {
        return id;
    }
}
