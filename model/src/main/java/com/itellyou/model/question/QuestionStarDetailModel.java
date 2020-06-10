package com.itellyou.model.question;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class QuestionStarDetailModel extends QuestionStarModel {
    @JSONField(label = "base")
    private QuestionDetailModel question;
    @JSONField(label = "base")
    private UserInfoModel user;

    public QuestionStarDetailModel(QuestionStarModel model){
        super(model.getQuestionId(),model.getCreatedTime(),model.getCreatedUserId(),model.getCreatedIp());
    }
}
