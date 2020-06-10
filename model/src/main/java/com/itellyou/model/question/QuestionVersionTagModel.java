package com.itellyou.model.question;

import com.itellyou.util.CacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionVersionTagModel implements CacheEntity {

    private Long versionId;

    private Long tagId;

    @Override
    public String cacheKey() {
        return new StringBuilder(versionId.toString()).append("-").append(tagId).toString();
    }
}
