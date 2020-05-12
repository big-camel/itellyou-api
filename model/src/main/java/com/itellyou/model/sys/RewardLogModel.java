package com.itellyou.model.sys;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.user.UserBankType;
import com.itellyou.util.DateUtils;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpDeserializer;
import com.itellyou.util.serialize.IpSerializer;
import com.itellyou.util.serialize.TimestampDeserializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JSONDefault(includes = "base")
public class RewardLogModel implements CacheEntity{
    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private UserBankType bankType;
    @JSONField(label = "base")
    private EntityType dataType;
    @JSONField(label = "base")
    private Long dataKey;
    @JSONField(label = "base")
    private Double amount;
    private Long userId;
    private Long createdUserId = 0l;
    @JSONField(label = "base",serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class)
    private Long createdTime = DateUtils.getTimestamp();
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp = 0l;

    @Override
    public String cacheKey() {
        return id.toString();
    }
}
