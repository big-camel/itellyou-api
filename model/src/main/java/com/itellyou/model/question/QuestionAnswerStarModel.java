package com.itellyou.model.question;

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
public class QuestionAnswerStarModel extends StarModel implements CacheEntity {
    private Long answerId;

    public QuestionAnswerStarModel(Long id, LocalDateTime createdTime, Long userId, Long ip) {
        super();
        this.answerId = id;
        this.setCreatedUserId(userId);
        this.setCreatedTime(createdTime);
        this.setCreatedIp(ip);
    }

    @Override
    public String cacheKey() {
        return answerId + "-" + getCreatedUserId();
    }
}
