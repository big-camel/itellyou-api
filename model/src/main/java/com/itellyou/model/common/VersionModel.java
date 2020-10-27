package com.itellyou.model.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.serialize.IpDeserializer;
import com.itellyou.util.serialize.IpSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersionModel {
    private Long id;
    private String content = "";
    private String html = "";
    private String description = "";
    private Integer version = 0;
    private boolean isReviewed = false;
    private boolean isDisabled = false;
    private boolean isPublished = false;
    private String remark;
    private String saveType;

    private LocalDateTime createdTime;
    private Long createdUserId;

    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;

    private LocalDateTime updatedTime;
    private Long updatedUserId;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long updatedIp;
}
