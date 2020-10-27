package com.itellyou.model.statistics;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityType;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class StatisticsInfoModel implements CacheEntity<String> {
    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private Long userId;
    @JSONField(label = "base,date")
    private LocalDate date;
    @JSONField(label = "base", serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private EntityType dataType;
    @JSONField(label = "base")
    private Long dataKey;
    @JSONField(label = "base,date")
    private int viewCount = 0;
    @JSONField(label = "base,date")
    private int commentCount = 0;
    @JSONField(label = "base,date")
    private int supportCount = 0;
    @JSONField(label = "base,date")
    private int opposeCount = 0;
    @JSONField(label = "base,date")
    private int starCount = 0;
    @JSONField(label = "base")
    private LocalDateTime createdTime;
    private Long createdUserId = 0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp = 0l;
    @JSONField(label = "base")
    private LocalDateTime updatedTime;
    private Long updatedUserId = 0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long updatedIp = 0l;

    @Override
    public String cacheKey() {
        return dataType + "-" + dataKey + "-" + date;
    }
}
