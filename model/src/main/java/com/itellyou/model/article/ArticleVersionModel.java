package com.itellyou.model.article;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.common.VersionModel;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.serialize.EnumSerializer;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ArticleVersionModel extends VersionModel implements CacheEntity<String> {
    private Long articleId = 0l;
    @JSONField(label = "draft,base")
    private Long columnId;
    @JSONField(serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private ArticleSourceType sourceType=ArticleSourceType.ORIGINAL;
    private String sourceData="";
    private String title = "";

    public ArticleVersionModel(Long id, Long articleId, Long columnId, ArticleSourceType sourceType, String sourceData, String title, String content, String html, String description, Integer version, Boolean isReviewed, Boolean isDisabled, Boolean isPublished, String remark, String saveType, LocalDateTime createdTime,Long createdUserId,Long createdIp,LocalDateTime updatedTime,Long updatedUserId,Long updatedIp){
        super(id,content,html,description,version,isReviewed,isDisabled,isPublished,remark,saveType,createdTime,createdUserId,createdIp,updatedTime,updatedUserId,updatedIp);
        this.articleId = articleId;
        this.columnId = columnId;
        this.sourceType = sourceType;
        this.sourceData = sourceData;
        this.title = title;
    }

    @Override
    public String cacheKey() {
        return articleId + "-" + super.getVersion();
    }
}
