package com.itellyou.model.question;

import com.itellyou.model.common.DataUpdateStepModel;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionUpdateStepModel extends DataUpdateStepModel {

    private Integer answerStep = 0;

    public QuestionUpdateStepModel(DataUpdateStepModel stepModel){
        super(stepModel.getId(),stepModel.getViewStep(),stepModel.getCommentStep(),stepModel.getSupportStep(),stepModel.getOpposeStep(),stepModel.getStarStep());
    }
}
