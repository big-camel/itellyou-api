package com.itellyou.model.user;

import com.itellyou.model.common.StarModel;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JSONDefault(includes = "base")
public class UserStarModel extends StarModel implements CacheEntity {
    private Long userId;

    public UserStarModel(Long id, Long createdTime, Long userId, Long ip) {
        super();
        this.userId = id;
        this.setCreatedUserId(userId);
        this.setCreatedTime(createdTime);
        this.setCreatedIp(ip);
    }

    @Override
    public String cacheKey() {
        return userId + "-" + getCreatedUserId();
    }
}
