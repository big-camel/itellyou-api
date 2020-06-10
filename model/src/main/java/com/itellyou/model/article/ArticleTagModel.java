package com.itellyou.model.article;

import com.itellyou.util.CacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleTagModel implements CacheEntity {

    private Long articleId;

    private Long tagId;

    @Override
    public String cacheKey() {
        return new StringBuilder(articleId.toString()).append("-").append(tagId).toString();
    }
}
