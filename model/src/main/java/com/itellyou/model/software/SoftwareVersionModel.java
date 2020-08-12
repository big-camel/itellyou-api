package com.itellyou.model.software;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.article.ArticleSourceType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.serialize.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareVersionModel implements CacheEntity {
    private Long id;
    private Long softwareId = 0l;
    @JSONField(label = "draft,base")
    private Long groupId;
    private String name = "";
    private String logo = "";
    private String content = "";
    private String html = "";
    private String description = "";
    private List<TagDetailModel> tags;
    private Integer version = 0;
    private boolean isReviewed = false;
    private boolean isDisabled = false;
    private boolean isPublished = false;
    private String remark;
    private String saveType;
    @JSONField(serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class)
    private Long createdTime;
    @JSONField(serialize = false)
    private Long createdUserId;
    private UserInfoModel author;
    @JSONField(serialize = false,serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;
    @JSONField(serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class)
    private Long updatedTime;
    @JSONField(serialize = false)
    private Long updatedUserId;
    @JSONField(serialize = false,serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long updatedIp;

    @Override
    public String cacheKey() {
        return String.valueOf(id);
    }
}
