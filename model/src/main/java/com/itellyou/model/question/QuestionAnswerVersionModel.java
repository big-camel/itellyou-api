package com.itellyou.model.question;

import com.itellyou.model.common.VersionModel;
import com.itellyou.util.CacheEntity;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerVersionModel extends VersionModel implements CacheEntity {
    private Long answerId = 0l;

    public QuestionAnswerVersionModel(Long id, Long answerId, String content, String html, String description, Integer version, Boolean isReviewed, Boolean isDisabled, Boolean isPublished, String remark, String saveType, LocalDateTime createdTime, Long createdUserId, Long createdIp, LocalDateTime updatedTime, Long updatedUserId, Long updatedIp){
        super(id,content,html,description,version,isReviewed,isDisabled,isPublished,remark,saveType,createdTime,createdUserId,createdIp,updatedTime,updatedUserId,updatedIp);
        this.answerId = answerId;
    }

    @Override
    public String cacheKey() {
        return answerId + "-" + super.getVersion();
    }
}
