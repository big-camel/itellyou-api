package com.itellyou.model.question;

import com.itellyou.util.CacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerTotalModel implements CacheEntity<Long> {
    private Long userId;
    private Integer totalCount;
    private Integer viewCount;
    private Integer supportCount;
    private Integer opposeCount;
    private Integer starCount;
    private Integer commentCount;

    @Override
    public Long cacheKey() {
        return userId;
    }
}
