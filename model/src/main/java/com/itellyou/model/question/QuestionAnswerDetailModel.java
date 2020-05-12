package com.itellyou.model.question;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.collab.CollabInfoModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class QuestionAnswerDetailModel extends QuestionAnswerModel {
    @JSONField(label = "question")
    private QuestionDetailModel question;
    @JSONField(label = "draft,base")
    private String content = "";
    @JSONField(label = "draft,base")
    private String html = "";
    @JSONField(label = "draft,base")
    private String description = "";
    @JSONField(label = "draft,base")
    private UserDetailModel author;
    @JSONField(label = "collab")
    private CollabInfoModel collab;
    @JSONField(label = "base")
    private boolean useSupport;
    @JSONField(label = "base")
    private boolean useOppose;
    @JSONField(label = "base")
    private boolean useStar;
    @JSONField(label = "base")
    private boolean useAuthor;
    @JSONField(label = "base")
    private boolean allowDelete;
    @JSONField(label = "base")
    private boolean allowSupport;
    @JSONField(label = "base")
    private boolean allowOppose;
    @JSONField(label = "base")
    private boolean allowReply=false;
    @JSONField(label = "base")
    private boolean allowEdit=false;
    @JSONField(label = "base")
    private boolean allowAdopt;
    @JSONField(label = "base")
    private boolean allowStar;
}
