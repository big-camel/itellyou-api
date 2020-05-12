package com.itellyou.model.upload;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.CacheEntity;
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
public class UploadFileModel implements CacheEntity {
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
    @JSONField(serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class,label = "base")
    private Long createdTime=0l;
    private Long createdUserId=0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp=0l;

    @Override
    public String cacheKey() {
        return key;
    }
}
