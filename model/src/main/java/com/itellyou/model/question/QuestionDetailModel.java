package com.itellyou.model.question;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.collab.CollabInfoModel;
import com.itellyou.model.sys.RewardType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.EnumSerializer;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class QuestionDetailModel extends QuestionInfoModel {
    @JSONField(label = "draft,base")
    private String title = "";
    @JSONField(label = "draft,base")
    private String content = "";
    @JSONField(label = "draft,base")
    private String html = "";
    @JSONField(label = "draft,base")
    private String description = "";
    @JSONField(label = "base")
    private boolean useStar;
    @JSONField(label = "base")
    private boolean useAuthor;
    @JSONField(label = "draft,base",serializeUsing = EnumSerializer.class)
    private RewardType rewardType=RewardType.DEFAULT;
    @JSONField(label = "draft,base")
    private Double rewardValue=0.0;
    @JSONField(label = "draft,base")
    private Double rewardAdd=0.0;
    @JSONField(label = "draft,base")
    private List<TagDetailModel> tags=new ArrayList<>();
    @JSONField(label = "base")
    private List<QuestionAnswerDetailModel> answerList;
    @JSONField(label = "draft,base")
    private UserDetailModel author;
    @JSONField(label = "collab")
    private CollabInfoModel collab;
}
