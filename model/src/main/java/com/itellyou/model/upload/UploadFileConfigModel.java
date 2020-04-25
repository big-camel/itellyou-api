package com.itellyou.model.upload;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.IpLongSerializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JSONDefault(includes = "base")
public class UploadFileConfigModel {
    @JSONField(label = "base")
    private Long id;
    @JSONField(label = "base")
    private String name;
    @JSONField(label = "base")
    private boolean isImage;
    @JSONField(label = "base")
    private boolean isVideo;
    @JSONField(label = "base")
    private boolean isFile;
    @JSONField(label = "base")
    private boolean isDoc;
    @JSONField(serializeUsing = TimestampSerializer.class,label = "base")
    private Long createdTime=0l;
    @JSONField(label = "base")
    private Long createdUserId=0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp=0l;
}
