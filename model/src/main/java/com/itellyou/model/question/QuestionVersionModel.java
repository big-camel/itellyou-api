package com.itellyou.model.question;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.common.VersionModel;
import com.itellyou.model.sys.RewardType;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.serialize.EnumSerializer;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionVersionModel extends VersionModel implements CacheEntity {
    private Long id;
    private Long questionId = 0l;
    private String title = "";
    @JSONField(serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private RewardType rewardType=RewardType.DEFAULT;
    private Double rewardValue=0.0;
    private Double rewardAdd=0.0;

    public QuestionVersionModel(Long id, Long questionId,String title,RewardType rewardType,Double rewardValue,Double rewardAdd, String content, String html, String description, Integer version, Boolean isReviewed, Boolean isDisabled, Boolean isPublished, String remark, String saveType, LocalDateTime createdTime, Long createdUserId, Long createdIp, LocalDateTime updatedTime, Long updatedUserId, Long updatedIp){
        super(id,content,html,description,version,isReviewed,isDisabled,isPublished,remark,saveType,createdTime,createdUserId,createdIp,updatedTime,updatedUserId,updatedIp);
        this.questionId = questionId;
        this.title = title;
        this.rewardType = rewardType;
        this.rewardValue = rewardValue;
        this.rewardAdd = rewardAdd;
    }

    @Override
    public String cacheKey() {
        return questionId + "-" + getVersion();
    }
}
