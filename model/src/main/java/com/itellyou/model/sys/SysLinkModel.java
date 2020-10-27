package com.itellyou.model.sys;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.DateUtils;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpDeserializer;
import com.itellyou.util.serialize.IpSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JSONDefault(includes = "base")
public class SysLinkModel implements CacheEntity {

    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private String text;
    @JSONField(label = "base")
    private String link;
    @JSONField(label = "base")
    private String target;
    @JSONField(label = "base")
    private LocalDateTime createdTime = DateUtils.toLocalDateTime();
    private Long createdUserId = 0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp = 0l;

    @Override
    public Long cacheKey() {
        return id;
    }
}
