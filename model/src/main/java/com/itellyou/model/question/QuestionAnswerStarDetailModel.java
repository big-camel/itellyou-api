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
public class QuestionAnswerStarDetailModel extends QuestionAnswerStarModel {
    @JSONField(label = "base")
    private QuestionAnswerDetailModel answer;
    @JSONField(label = "base")
    private UserInfoModel user;
}
