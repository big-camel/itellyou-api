package com.itellyou.model.upload;

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
public class UploadFileModel {
    @JSONField(label = "base")
    private String key;
    @JSONField(label = "base")
    private String name;
    @JSONField(label = "base")
    private String extname;
    @JSONField(label = "base")
    private String domain;
    @JSONField(label = "base")
    public String getUrl(){
        return this.domain + "/" + this.key;
    }
    private String bucket;
    private UploadSource source;
    @JSONField(label = "base")
    private Long size;
    @JSONField(serializeUsing = TimestampSerializer.class,label = "base")
    private Long createdTime=0l;
    private Long createdUserId=0l;
    @JSONField(serializeUsing = IpLongSerializer.class)
    private Long createdIp=0l;
}
