package com.itellyou.model.article;

import com.itellyou.util.CacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleVersionTagModel implements CacheEntity {

    private Long version;

    private Long tag;

    @Override
    public String cacheKey() {
        return new StringBuilder(version.toString()).append("-").append(tag).toString();
    }
}
