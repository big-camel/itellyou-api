package com.itellyou.model.article;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpDeserializer;
import com.itellyou.util.serialize.IpSerializer;
import com.itellyou.util.serialize.TimestampDeserializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class ArticleCommentModel implements CacheEntity {
    @JSONField(label = "base")
    private Long id;
    private Long articleId;
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
    private Integer support;
    @JSONField(label = "base")
    private Integer oppose;
    @JSONField(serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class , label = "base")
    private Long createdTime = 0l;
    private Long createdUserId = 0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp = 0l;
    @JSONField(serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class, label = "base")
    private Long updatedTime = 0l;
    private Long updatedUserId = 0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long updatedIp = 0l;

    @Override
    public String cacheKey() {
        return id.toString();
    }
}
