package com.itellyou.model.tag;

import com.itellyou.model.common.VersionModel;
import com.itellyou.util.CacheEntity;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class TagVersionModel extends VersionModel implements CacheEntity {
    private Long tagId=0l;
    private String icon = "";

    public TagVersionModel(Long id, Long tagId, String icon, String content, String html, String description, Integer version, Boolean isReviewed, Boolean isDisabled, Boolean isPublished, String remark, String saveType, LocalDateTime createdTime, Long createdUserId, Long createdIp, LocalDateTime updatedTime, Long updatedUserId, Long updatedIp){
        super(id,content,html,description,version,isReviewed,isDisabled,isPublished,remark,saveType,createdTime,createdUserId,createdIp,updatedTime,updatedUserId,updatedIp);
        this.tagId = tagId;
        this.icon = icon;
    }

    @Override
    public String cacheKey() {
        return tagId + "-" + getVersion();
    }
}
