package com.itellyou.model.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityType;
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
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class ViewInfoModel implements CacheEntity<String> {
    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private String title;
    @JSONField(label = "base")
    private String os;
    @JSONField(label = "base")
    private String browser;
    @JSONField(label = "base", serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private EntityType dataType;
    @JSONField(label = "base")
    private Long dataKey;
    private Long createdUserId;
    @JSONField(label = "base")
    private LocalDateTime createdTime;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;
    private Long updatedUserId;
    @JSONField(label = "base")
    private LocalDateTime updatedTime;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long updatedIp;

    public ViewInfoModel(String title,String os,String browser,EntityType dataType,Long dataKey,Long userId,Long ip){
        this.title = title;
        this.os = os;
        this.browser = browser;
        this.dataKey = dataKey;
        this.dataType = dataType;
        this.createdUserId = userId;
        this.updatedUserId = userId;
        this.createdIp = ip;
        this.updatedIp = ip;
        this.createdTime = DateUtils.toLocalDateTime();
        this.updatedTime = DateUtils.toLocalDateTime();
    }

    @Override
    public String cacheKey() {
        return (createdUserId != null && createdUserId > 0 ? createdUserId : createdIp) + "-" + dataType.getValue() + "-" + dataKey;
    }
}
