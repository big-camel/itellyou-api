package com.itellyou.model.article;

import com.itellyou.util.CacheEntity;
import com.itellyou.model.user.UserBankType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticlePaidReadModel implements CacheEntity {
    private Long articleId;
    private UserBankType paidType;
    private Boolean paidToRead;
    private Double paidAmount;
    private Boolean starToRead;
    private Double freeReadScale;

    @Override
    public String cacheKey() {
        return articleId.toString();
    }
}
