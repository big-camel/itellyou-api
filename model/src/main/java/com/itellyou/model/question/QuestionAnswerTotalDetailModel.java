package com.itellyou.model.question;

import com.itellyou.model.user.UserDetailModel;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerTotalDetailModel extends QuestionAnswerTotalModel {
    private UserDetailModel user;

    public QuestionAnswerTotalDetailModel(QuestionAnswerTotalModel totalModel){
        super(totalModel.getUserId(),totalModel.getTotalCount(),totalModel.getViewCount(),totalModel.getSupportCount(),totalModel.getOpposeCount(),totalModel.getStarCount(),totalModel.getCommentCount());
    }
}
