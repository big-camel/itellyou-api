package com.itellyou.model.question;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.RewardType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.serialize.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionVersionModel implements CacheEntity {
    private Long id;
    private Long questionId = 0l;
    private String title = "";
    private String content = "";
    private String html = "";
    private String description = "";
    private List<TagDetailModel> tags = new ArrayList<>();
    private Integer version = 0;
    private boolean isReviewed = false;
    private boolean isDisabled = false;
    private boolean isPublished = false;
    private String remark;
    @JSONField(serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private RewardType rewardType=RewardType.DEFAULT;
    private Double rewardValue=0.0;
    private Double rewardAdd=0.0;
    private String saveType;
    @JSONField(serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class)
    private Long createdTime;
    @JSONField(serialize = false)
    private Long createdUserId;
    private UserInfoModel author;
    @JSONField(serialize = false,serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long createdIp;
    @JSONField(serializeUsing = TimestampSerializer.class,deserializeUsing = TimestampDeserializer.class)
    private Long updatedTime;
    @JSONField(serialize = false)
    private Long updatedUserId;
    @JSONField(serialize = false,serializeUsing = IpSerializer.class,deserializeUsing = IpDeserializer.class)
    private Long updatedIp;

    @Override
    public String cacheKey() {
        return id.toString();
    }
}
