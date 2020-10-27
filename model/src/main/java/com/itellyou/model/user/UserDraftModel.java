package com.itellyou.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.EntityType;
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
    @JSONField( label = "base")
    private LocalDateTime createdTime;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;
    private Long createdUserId=0l;
}
