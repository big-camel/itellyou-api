package com.itellyou.model.collab;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.serialize.IpLongSerializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollabInfoModel {
    private Long id;
    private String key;
    private String token;
    private String host;
    private boolean isDisabled;
    @JSONField(serializeUsing = TimestampSerializer.class)
    private Long createdTime = 0l;
    private Long createdUserId = 0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp = 0l;
}
