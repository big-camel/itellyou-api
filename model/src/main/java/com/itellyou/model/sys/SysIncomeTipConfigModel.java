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
public class SysIncomeTipConfigModel implements CacheEntity<Long> {
    @JSONField( label = "base")
    private Long id;
    @JSONField( label = "base")
    private String name;
    @JSONField(label = "base")
    private EntityType dataType;
    @JSONField( label = "base")
    private Integer minView;
    @JSONField( label = "base")
    private Integer minComment;
    @JSONField( label = "base")
    private Integer minSupport;
    @JSONField( label = "base")
    private Integer minOppose;
    @JSONField( label = "base")
    private Integer minStar;
    @JSONField( label = "base")
    private Double  viewWeight=0.00;
    @JSONField( label = "base")
    private Double  commentWeight=0.00;
    @JSONField( label = "base")
    private Double  supportWeight=0.00;
    @JSONField( label = "base")
    private Double  opposeWeight=0.00;
    @JSONField( label = "base")
    private Double  starWeight=0.00;
    @JSONField( label = "base")
    private Double minAmount=0.00;
    @JSONField( label = "base")
    private Double maxAmount=0.00;
    @JSONField( label = "base")
    private Integer maxUserCount=0;
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
