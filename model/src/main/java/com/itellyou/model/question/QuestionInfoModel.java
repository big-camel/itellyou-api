package com.itellyou.model.question;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.RewardType;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.EnumSerializer;
import com.itellyou.util.serialize.IpDeserializer;
import com.itellyou.util.serialize.IpSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class QuestionInfoModel implements CacheEntity {
    @JSONField(label = "draft,base")
    private Long id;
    @JSONField(label = "draft,base")
    private String title = "";
    @JSONField(label = "draft,base")
    private String description = "";
    @JSONField(label = "draft,base",serializeUsing = EnumSerializer.class, deserializeUsing = EnumSerializer.class)
    private RewardType rewardType=RewardType.DEFAULT;
    @JSONField(label = "draft,base")
    private Double rewardValue=0.0;
    @JSONField(label = "draft,base")
    private Double rewardAdd=0.0;
    @JSONField(label = "base")
    private Integer version = 0;
    @JSONField(label = "draft,base")
    private boolean isPublished = false;
    @JSONField(label = "draft,base")
    private boolean isAdopted = false;
    private boolean isDisabled = false;
    private boolean isDeleted = false;
    @JSONField(label = "draft,base",name = "draft_version")
    private Integer draft = 0;
    @JSONField(label = "draft,base")
    private String cover = "";
    @JSONField(label = "base")
    private Long adoptionId = 0l;
    @JSONField(label = "base")
    private Integer answerCount = 0;
    @JSONField(label = "base")
    private Integer commentCount = 0;
    @JSONField(label = "base")
    private Integer viewCount = 0;
    @JSONField(label = "base")
    private Integer supportCount = 0;
    @JSONField(label = "base")
    private Integer opposeCount = 0;
    @JSONField(label = "base")
    private Integer starCount = 0;
    @JSONField(label = "draft,base")
    private LocalDateTime createdTime;
    private Long createdUserId = 0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp = 0l;
    @JSONField(label = "draft,base")
    private LocalDateTime updatedTime;
    private Long updatedUserId = 0l;
    @JSONField(serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long updatedIp = 0l;

    @Override
    public Long cacheKey() {
        return id;
    }
}
