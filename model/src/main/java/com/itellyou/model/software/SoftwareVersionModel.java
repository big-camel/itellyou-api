package com.itellyou.model.software;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.common.VersionModel;
import com.itellyou.util.CacheEntity;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class SoftwareVersionModel extends VersionModel implements CacheEntity {
    private Long softwareId = 0l;
    @JSONField(label = "draft,base")
    private Long groupId;
    private String name = "";
    private String logo = "";

    public SoftwareVersionModel(Long id, Long softwareId, Long groupId, String name, String logo, String content, String html, String description, Integer version, Boolean isReviewed, Boolean isDisabled, Boolean isPublished, String remark, String saveType, LocalDateTime createdTime, Long createdUserId, Long createdIp, LocalDateTime updatedTime, Long updatedUserId, Long updatedIp){
        super(id,content,html,description,version,isReviewed,isDisabled,isPublished,remark,saveType,createdTime,createdUserId,createdIp,updatedTime,updatedUserId,updatedIp);
        this.softwareId = softwareId;
        this.groupId = groupId;
        this.name = name;
        this.logo = logo;
    }

    @Override
    public String cacheKey() {
        return softwareId + "-" + getVersion();
    }
}
