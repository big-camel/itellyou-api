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
public class QuestionAnswerCommentDetailModel extends QuestionAnswerCommentModel {
    @JSONField(label = "base")
    private QuestionAnswerCommentDetailModel reply;
    @JSONField(label = "base")
    private List<QuestionAnswerCommentModel> child;
    @JSONField(label = "answer")
    private QuestionAnswerDetailModel answer;
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

    public QuestionAnswerCommentDetailModel(QuestionAnswerCommentModel model){
        super(model.getId(),model.getAnswerId(),model.getParentId(),model.getReplyId(),model.isDeleted(),model.getContent(),model.getHtml(),model.getCommentCount(),model.getSupportCount(),model.getOpposeCount(),model.getCreatedTime(),model.getCreatedUserId(),model.getCreatedIp(),model.getUpdatedTime(),model.getUpdatedUserId(),model.getUpdatedIp());
    }
}
