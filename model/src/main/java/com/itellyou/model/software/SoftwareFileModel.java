package com.itellyou.model.software;

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
public class SoftwareFileModel implements CacheEntity {
    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private Long updaterId;
    @JSONField(label = "base")
    private String title;
    @JSONField(label = "base")
    private String name;
    @JSONField(label = "base")
    private String suffix;
    @JSONField(label = "base")
    private Boolean isRecommend;
    @JSONField(label = "base")
    private Long size;
    @JSONField(label = "base")
    private String sha1;
    @JSONField(label = "base")
    private String sha256;
    @JSONField(label = "base")
    private String md5;
    @JSONField(label = "base")
    private String ed2k;
    @JSONField(label = "base")
    private String magnet;
    @JSONField( label = "base")
    private LocalDateTime publishDate;
    @JSONField( label = "base")
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
    public Long cacheKey() {
        return id;
    }
}
