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
public class SoftwareCommentModel implements CacheEntity {
    @JSONField(label = "base")
    private Long id;
    private Long softwareId;
    @JSONField(label = "base")
    private Long parentId=0L;
    private Long replyId=0L;
    @JSONField(label = "base")
    private boolean isDeleted;
    @JSONField(label = "base")
    private String content;
    private String html;
    @JSONField(label = "base")
    private Integer commentCount;
    @JSONField(label = "base")
    private Integer supportCount;
    @JSONField(label = "base")
    private Integer opposeCount;
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
