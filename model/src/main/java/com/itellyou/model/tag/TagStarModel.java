package com.itellyou.model.tag;

import com.itellyou.model.common.StarModel;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JSONDefault(includes = "base")
public class TagStarModel extends StarModel implements CacheEntity {
    private Long tagId;
    public TagStarModel(Long id, LocalDateTime createdTime, Long userId, Long ip) {
        super();
        this.tagId = id;
        this.setCreatedUserId(userId);
        this.setCreatedTime(createdTime);
        this.setCreatedIp(ip);
    }

    @Override
    public String cacheKey() {
        return tagId.toString() + "-" + getCreatedUserId();
    }
}
