package com.itellyou.model.tag;

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
public class TagInfoModel implements CacheEntity {
    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private String name;
    @JSONField(label = "draft,base")
    private String description = "";
    private Long groupId=0l;
    @JSONField(label = "draft,base")
    private boolean isPublished = false;
    @JSONField(label = "base")
    private boolean isDisabled = false;
    @JSONField(label = "base")
    private Integer starCount=0;
    @JSONField(label = "base")
    private Integer articleCount=0;
    @JSONField(label = "base")
    private Integer questionCount=0;
    @JSONField(label = "base")
    private Integer version=0;
    @JSONField(label = "draft",name = "draft_version")
    private Integer draft = 0;
    @JSONField(label = "base")
    private LocalDateTime createdTime;
    private Long createdUserId=0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp=0l;

    private LocalDateTime updatedTime;
    private Long updatedUserId=0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long updatedIp=0l;

    public TagInfoModel(Long id , String name , Long groupId){
        this.id = id;
        this.name = name;
        this.groupId = groupId;
    }

    @Override
    public Long cacheKey() {
        return id;
    }
}
