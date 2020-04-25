package com.itellyou.model.article;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.util.serialize.IpLongSerializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVersionModel {
    private Long id;
    private Long articleId = 0l;
    @JSONField(label = "draft,base")
    private Long columnId;
    private ArticleSourceType sourceType=ArticleSourceType.ORIGINAL;
    private String sourceData="";
    private String title = "";
    private String content = "";
    private String html = "";
    private String description = "";
    private List<TagInfoModel> tags;
    private Integer version = 0;
    private boolean isReviewed = false;
    private boolean isDisabled = false;
    private boolean isPublished = false;
    private String remark;
    private String saveType;
    @JSONField(serializeUsing = TimestampSerializer.class)
    private Long createdTime;
    @JSONField(serialize = false)
    private Long createdUserId;
    private UserInfoModel author;
    @JSONField(serialize = false,serializeUsing = IpLongSerializer.class)
    private Long createdIp;
    @JSONField(serializeUsing = TimestampSerializer.class)
    private Long updatedTime;
    @JSONField(serialize = false)
    private Long updatedUserId;
    @JSONField(serialize = false,serializeUsing = IpLongSerializer.class)
    private Long updatedIp;
}
