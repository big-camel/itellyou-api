package com.itellyou.model.question;

import com.itellyou.model.common.StarModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JSONDefault(includes = "base")
public class QuestionAnswerStarModel extends StarModel {
    private Long answerId;

    public QuestionAnswerStarModel(Long id, Long createdTime, Long userId, Long ip) {
        super();
        this.answerId = id;
        this.setCreatedUserId(userId);
        this.setCreatedTime(createdTime);
        this.setCreatedIp(ip);
    }
}
