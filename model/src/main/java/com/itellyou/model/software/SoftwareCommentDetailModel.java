package com.itellyou.model.software;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JSONDefault(includes = "base")
public class SoftwareCommentDetailModel extends SoftwareCommentModel implements CacheEntity {
    @JSONField(label = "base")
    private SoftwareCommentDetailModel reply;
    @JSONField(label = "base")
    private List<SoftwareCommentDetailModel> child;
    @JSONField(label = "software")
    private SoftwareDetailModel software;
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

    public SoftwareCommentDetailModel(SoftwareCommentModel model){
        super(model.getId(),model.getSoftwareId(),model.getParentId(),model.getReplyId(),model.isDeleted(),model.getContent(),model.getHtml(),model.getCommentCount(),model.getSupport(),model.getOppose(),model.getCreatedTime(),model.getCreatedUserId(),model.getCreatedIp(),model.getUpdatedTime(),model.getUpdatedUserId(),model.getUpdatedIp());
    }

    @Override
    public String cacheKey() {
        return this.getId().toString();
    }
}
