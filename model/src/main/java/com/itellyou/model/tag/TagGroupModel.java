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
public class TagGroupModel {
    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private String name;
    @JSONField(label = "base")
    private Integer tagCount=0;
    @JSONField(label = "base")
    private List<TagInfoModel> tagList;
    @JSONField(serializeUsing = TimestampSerializer.class,label = "base")
    private Long createdTime;
    private Long createdUserId;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp;
}
