package com.itellyou.model.question;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.collab.CollabInfoModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.util.annotation.JSONDefault;
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
    private String content = "";
    @JSONField(label = "draft,base")
    private String html = "";
    @JSONField(label = "base")
    private boolean useStar;
    @JSONField(label = "base")
    private boolean useAuthor;
    @JSONField(label = "draft,base")
    private List<TagDetailModel> tags=new ArrayList<>();
    @JSONField(label = "base")
    private List<QuestionAnswerDetailModel> answerList;
    @JSONField(label = "draft,base")
    private UserDetailModel author;
    @JSONField(label = "collab")
    private CollabInfoModel collab;

    public QuestionDetailModel(QuestionInfoModel model){
        super(model.getId(),model.getTitle(),model.getDescription(),model.getRewardType(),model.getRewardValue(),model.getRewardAdd(),model.getVersion(),model.isPublished(),model.isAdopted(),model.isDisabled(),model.isDeleted(),model.getDraft(),model.getCover(),model.getAdoptionId(),model.getAnswers(),model.getComments(),model.getView(),model.getSupport(),model.getOppose(),model.getStarCount(),model.getCreatedTime(),model.getCreatedUserId(),model.getCreatedIp(),model.getUpdatedTime(),model.getUpdatedUserId(),model.getUpdatedIp());
    }
}
