package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityType;
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
public class UserDraftModel {
    @JSONField(label = "base")
    private Long authorId;
    @JSONField(label = "base")
    private String url;
    @JSONField(label = "base")
    private String title;
    @JSONField(label = "base")
    private String content;
    @JSONField(label = "base")
    private EntityType dataType;
    @JSONField(label = "base")
    private Long dataKey;
    @JSONField(serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class , label = "base")
    private Long createdTime;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;
    private Long createdUserId=0l;
}
