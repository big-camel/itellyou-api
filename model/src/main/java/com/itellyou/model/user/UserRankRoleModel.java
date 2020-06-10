package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.CacheEntity;
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
public class UserRankRoleModel implements CacheEntity {
    private Long rankId;
    private Long roleId;
    @JSONField(serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class)
    private Long createdTime = 0l;
    private Long createdUserId = 0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp = 0l;

    @Override
    public String cacheKey() {
        return rankId.toString() + "_" + roleId.toString();
    }
}
