package com.itellyou.model.software;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.VoteType;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.serialize.EnumSerializer;
import com.itellyou.util.serialize.IpDeserializer;
import com.itellyou.util.serialize.IpSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoftwareVoteModel implements CacheEntity {
    private Long softwareId;
    @JSONField(serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private VoteType type;
    @JSONField(label = "base")
    private LocalDateTime createdTime;
    private Long createdUserId = 0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp = 0l;

    @Override
    public String cacheKey() {
        return softwareId.toString() + "-" + createdUserId;
    }
}
