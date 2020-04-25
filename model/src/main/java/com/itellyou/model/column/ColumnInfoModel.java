package com.itellyou.model.column;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpLongSerializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class ColumnInfoModel {
    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private String name="";
    @JSONField(label = "base")
    private String avatar="";
    @JSONField(label = "base")
    private String description="";
    @JSONField(label = "base")
    private boolean isDisabled = false;
    @JSONField(label = "base")
    private boolean isDeleted = false;
    @JSONField(label = "base")
    private boolean isReviewed = false;
    @JSONField(label = "base")
    private Integer memberCount = 0;
    @JSONField(label = "base")
    private Integer articleCount = 0;
    @JSONField(label = "base")
    private Integer starCount = 0;
    @JSONField(serializeUsing = TimestampSerializer.class,label = "base")
    private Long createdTime = 0l;
    private Long createdUserId = 0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp = 0l;
    @JSONField(serializeUsing = TimestampSerializer.class,label = "base")
    private Long updatedTime = 0l;
    private Long updatedUserId = 0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long updatedIp = 0l;

    public ColumnInfoModel(Long id,String name,String avatar,String description,Long updatedUserId,Long updatedTime,Long updatedIp){
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.description = description;
        this.updatedUserId = updatedUserId;
        this.updatedTime = updatedTime;
        this.updatedIp = updatedIp;
    }
}
