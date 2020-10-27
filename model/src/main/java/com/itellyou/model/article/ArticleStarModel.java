package com.itellyou.model.article;

import com.itellyou.model.common.StarModel;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

import java.time.LocalDateTime;


@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JSONDefault(includes = "base")
public class ArticleStarModel extends StarModel implements CacheEntity {
    private Long articleId;

    public ArticleStarModel(Long id, LocalDateTime createdTime, Long userId, Long ip){
        super();
        this.articleId = id;
        this.setCreatedUserId(userId);
        this.setCreatedTime(createdTime);
        this.setCreatedIp(ip);
    }

    @Override
    public String cacheKey() {
        return articleId.toString() + "-" + getCreatedUserId();
    }
}
