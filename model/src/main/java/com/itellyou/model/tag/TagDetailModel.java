package com.itellyou.model.tag;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.collab.CollabInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class TagDetailModel extends TagInfoModel {
    @JSONField(label = "draft,base")
    private String content = "";
    @JSONField(label = "draft,base")
    private String html = "";
    @JSONField(label = "draft,base")
    private String icon;
    @JSONField(label = "base")
    private boolean useStar;
    @JSONField(label = "draft,base")
    private UserInfoModel author;
    @JSONField(label = "base")
    private TagGroupModel group;
    @JSONField(label = "collab")
    private CollabInfoModel collab;

    public TagDetailModel(TagInfoModel model){
        super(model.getId(),model.getName(),model.getDescription(),model.getGroupId(),model.isPublished(),model.isDisabled(),model.getStarCount(),model.getArticleCount(),model.getQuestionCount(),model.getVersion(),model.getDraft(),model.getCreatedTime(),model.getCreatedUserId(),model.getCreatedIp(),model.getUpdatedTime(),model.getUpdatedUserId(),model.getUpdatedIp());
    }
}
