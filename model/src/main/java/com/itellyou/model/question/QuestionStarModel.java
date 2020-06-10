package com.itellyou.model.question;

import com.itellyou.model.common.StarModel;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JSONDefault(includes = "base")
public class QuestionStarModel extends StarModel implements CacheEntity {
    private Long questionId;

    public QuestionStarModel(Long id, Long createdTime, Long userId, Long ip) {
        super();
        this.questionId = id;
        this.setCreatedUserId(userId);
        this.setCreatedTime(createdTime);
        this.setCreatedIp(ip);
    }

    @Override
    public String cacheKey() {
        return questionId + "-" + getCreatedUserId();
    }
}
