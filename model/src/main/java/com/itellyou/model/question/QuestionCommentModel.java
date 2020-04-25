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
public class QuestionCommentModel {
    @JSONField(label = "base")
    private Long id;
    private Long questionId;
    @JSONField(label = "base")
    private Long parentId=0L;
    private Long replyId=0L;
    @JSONField(label = "base")
    private boolean isDeleted;
    @JSONField(label = "base")
    private String content;
    private String html;
    @JSONField(label = "base")
    private Integer comments;
    @JSONField(label = "base")
    private Integer support;
    @JSONField(label = "base")
    private Integer oppose;
    @JSONField(serializeUsing = TimestampSerializer.class , label = "base")
    private Long createdTime = 0l;
    private Long createdUserId = 0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp = 0l;
    @JSONField(serializeUsing = TimestampSerializer.class, label = "base")
    private Long updatedTime = 0l;
    private Long updatedUserId = 0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long updatedIp = 0l;
}
