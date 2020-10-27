package com.itellyou.model.sys;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.DateUtils;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.EnumSerializer;
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
public class ReportModel implements CacheEntity {

    private Long id;
    @JSONField(serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private ReportAction action;
    @JSONField(serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private EntityType type;
    private Integer state=0;
    private String description="";
    private Long targetId;
    private Long targetUserId;

    private LocalDateTime createdTime = DateUtils.toLocalDateTime();
    private Long createdUserId = 0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp = 0l;

    @Override
    public Long cacheKey() {
        return id;
    }
}
