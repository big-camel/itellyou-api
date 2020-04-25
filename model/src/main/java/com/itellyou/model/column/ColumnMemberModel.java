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
public class ColumnMemberModel {
    @JSONField(label = "base")
    private Long columnId;
    @JSONField(label = "base")
    private Long userId;
    @JSONField(serializeUsing = TimestampSerializer.class,label = "base")
    private Long createdTime = 0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp = 0l;
}
