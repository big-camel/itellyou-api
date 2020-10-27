package com.itellyou.model.tag;

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
public class TagGroupModel implements CacheEntity {
    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private String name;
    @JSONField(label = "base")
    private Integer tagCount=0;
    @JSONField(label = "base")
    private LocalDateTime createdTime;
    private Long createdUserId;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;

    @Override
    public Long cacheKey() {
        return id;
    }
}
