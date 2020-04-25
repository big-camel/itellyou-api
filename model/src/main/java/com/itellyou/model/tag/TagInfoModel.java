package com.itellyou.model.tag;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpLongSerializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class TagInfoModel {
    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private String name;
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
    @JSONField(serializeUsing = TimestampSerializer.class)
    private Long createdTime=0l;
    private Long createdUserId=0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp=0l;
    @JSONField(serializeUsing = TimestampSerializer.class)
    private Long updatedTime=0l;
    private Long updatedUserId=0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long updatedIp=0l;

    public TagInfoModel(Long id , String name , Long groupId){
        this.id = id;
        this.name = name;
        this.groupId = groupId;
    }
}
