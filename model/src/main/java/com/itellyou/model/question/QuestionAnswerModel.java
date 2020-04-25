package com.itellyou.model.question;

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
public class QuestionAnswerModel {
    @JSONField(label = "draft,base")
    private Long id;
    @JSONField(label = "draft,base")
    private Long questionId;
    @JSONField(label = "base")
    private Integer version;
    @JSONField(label = "draft,base",name = "draft_version")
    private Integer draft = 0;
    @JSONField(label = "draft,base")
    private String cover = "";
    @JSONField(label = "draft,base")
    private boolean isPublished = false;
    @JSONField(label = "base")
    private boolean isDisabled = false;
    @JSONField(label = "base")
    private boolean isDeleted = false;
    @JSONField(label = "base")
    private boolean isAdopted = false;
    @JSONField(label = "base")
    private Integer comments=0;
    @JSONField(label = "base")
    private Integer support=0;
    @JSONField(label = "base")
    private Integer oppose=0;
    @JSONField(label = "base")
    private Integer view=0;
    @JSONField(label = "base")
    private Integer starCount=0;
    @JSONField(serializeUsing = TimestampSerializer.class,label = "draft,base")
    private Long createdTime = 0l;
    private Long createdUserId = 0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp = 0l;
    @JSONField(serializeUsing = TimestampSerializer.class,label = "draft,base")
    private Long updatedTime = 0l;
    private Long updatedUserId = 0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long updatedIp = 0l;
}
