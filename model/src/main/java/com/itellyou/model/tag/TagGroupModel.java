package com.itellyou.model.tag;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpDeserializer;
import com.itellyou.util.serialize.IpSerializer;
import com.itellyou.util.serialize.TimestampDeserializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JSONField(label = "base" , serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class)
    private Long createdTime;
    private Long createdUserId;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;

    @Override
    public String cacheKey() {
        return id.toString();
    }
}
