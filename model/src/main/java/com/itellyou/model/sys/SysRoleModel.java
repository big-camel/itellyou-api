package com.itellyou.model.sys;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.DateUtils;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpDeserializer;
import com.itellyou.util.serialize.IpSerializer;
import com.itellyou.util.serialize.TimestampDeserializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.access.ConfigAttribute;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JSONDefault(includes = "base")
public class SysRoleModel implements ConfigAttribute, CacheEntity {
    @JSONField(label = "info")
    private Long id;
    @JSONField(label = "base,info")
    private String name;
    @JSONField(label = "info")
    private String description;
    @JSONField(label = "info")
    private boolean disabled;
    @JSONField(label = "info")
    private boolean system;
    @JSONField(label = "info",serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class)
    private Long createdTime = DateUtils.getTimestamp();
    @JSONField(label = "info")
    private Long createdUserId = 0l;
    @JSONField(label = "info",serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp = 0l;

    @Override
    public String cacheKey() {
        return String.valueOf(id);
    }

    @Override
    public String getAttribute() {
        return name;
    }
}
