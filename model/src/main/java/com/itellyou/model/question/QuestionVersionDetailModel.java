package com.itellyou.model.question;

import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserInfoModel;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionVersionDetailModel extends QuestionVersionModel {

    private List<TagDetailModel> tags = new ArrayList<>();

    private UserInfoModel author;

    public QuestionVersionDetailModel(QuestionVersionModel versionModel){
        super(versionModel.getId(),versionModel.getQuestionId(),versionModel.getTitle(),versionModel.getRewardType(),versionModel.getRewardValue(),versionModel.getRewardAdd(),versionModel.getContent(),versionModel.getHtml(),versionModel.getDescription(),versionModel.getVersion(),versionModel.isReviewed(),versionModel.isDisabled(),versionModel.isPublished(),versionModel.getRemark(),versionModel.getSaveType(),versionModel.getCreatedTime(),versionModel.getCreatedUserId(),versionModel.getCreatedIp(),versionModel.getUpdatedTime(),versionModel.getUpdatedUserId(),versionModel.getUpdatedIp());
    }
}
