package com.itellyou.model.question;

import com.itellyou.model.user.UserInfoModel;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerVersionDetailModel extends QuestionAnswerVersionModel {

    private UserInfoModel author;

    public QuestionAnswerVersionDetailModel(QuestionAnswerVersionModel versionModel){
        super(versionModel.getId(),versionModel.getAnswerId(),versionModel.getContent(),versionModel.getHtml(),versionModel.getDescription(),versionModel.getVersion(),versionModel.isReviewed(),versionModel.isDisabled(),versionModel.isPublished(),versionModel.getRemark(),versionModel.getSaveType(),versionModel.getCreatedTime(),versionModel.getCreatedUserId(),versionModel.getCreatedIp(),versionModel.getUpdatedTime(),versionModel.getUpdatedUserId(),versionModel.getUpdatedIp());
    }
}
