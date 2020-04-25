package com.itellyou.model.question;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.reward.RewardType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.util.serialize.IpLongSerializer;
import com.itellyou.util.serialize.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionVersionModel {
    private Long id;
    private Long questionId = 0l;
    private String title = "";
    private String content = "";
    private String html = "";
    private String description = "";
    private List<TagInfoModel> tags = new ArrayList<>();
    private Integer version = 0;
    private boolean isReviewed = false;
    private boolean isDisabled = false;
    private boolean isPublished = false;
    private String remark;
    private RewardType rewardType=RewardType.DEFAULT;
    private Double rewardValue=0.0;
    private Double rewardAdd=0.0;
    private String saveType;
    @JSONField(serializeUsing = TimestampSerializer.class)
    private Long createdTime;
    @JSONField(serialize = false)
    private Long createdUserId;
    private UserInfoModel author;
    @JSONField(serialize = false,serializeUsing = IpLongSerializer.class)
    private Long createdIp;
    @JSONField(serializeUsing = TimestampSerializer.class)
    private Long updatedTime;
    @JSONField(serialize = false)
    private Long updatedUserId;
    @JSONField(serialize = false,serializeUsing = IpLongSerializer.class)
    private Long updatedIp;
}
