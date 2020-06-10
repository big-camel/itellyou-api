package com.itellyou.model.question;

import com.itellyou.util.CacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionTagModel implements CacheEntity {

    private Long questionId;

    private Long tagId;

    @Override
    public String cacheKey() {
        return new StringBuilder(questionId.toString()).append("-").append(tagId).toString();
    }
}
