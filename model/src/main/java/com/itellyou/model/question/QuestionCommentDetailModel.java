package com.itellyou.model.question;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JSONDefault(includes = "base")
public class QuestionCommentDetailModel extends QuestionCommentModel {
    @JSONField(label = "base")
    private QuestionCommentDetailModel reply;
    @JSONField(label = "base")
    private List<QuestionCommentModel> child;
    @JSONField(label = "question")
    private QuestionDetailModel question;
    @JSONField(label = "base")
    private UserDetailModel author;
    @JSONField(label = "base")
    private boolean isHot;
    @JSONField(label = "base")
    private boolean useSupport;
    @JSONField(label = "base")
    private boolean useOppose;
    @JSONField(label = "base")
    private boolean useAuthor;
    @JSONField(label = "base")
    private boolean allowDelete;
    @JSONField(label = "base")
    private boolean allowSupport;
    @JSONField(label = "base")
    private boolean allowOppose;
    @JSONField(label = "base")
    private boolean allowReply;

}
