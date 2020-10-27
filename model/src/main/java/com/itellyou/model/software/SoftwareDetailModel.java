package com.itellyou.model.software;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.collab.CollabInfoModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class SoftwareDetailModel extends SoftwareInfoModel implements CacheEntity {
    @JSONField(label = "draft,base")
    private SoftwareGroupModel group;
    @JSONField(label = "draft,base")
    private String content = "";
    @JSONField(label = "draft,base")
    private String html = "";
    @JSONField(label = "draft,base")
    private List<TagDetailModel> tags = new ArrayList<>();
    @JSONField(label = "draft,base")
    private List<SoftwareAttributesModel> attributes = new ArrayList<>();
    @JSONField(label = "draft,base")
    private List<SoftwareReleaseDetailModel> releases = new ArrayList<>();
    @JSONField(label = "draft,base")
    private UserInfoModel author;
    @JSONField(label = "collab")
    private CollabInfoModel collab;
    @JSONField(label = "base")
    private boolean allowEdit;
    @JSONField(label = "base")
    private boolean allowSupport;
    @JSONField(label = "base")
    private boolean allowOppose;
    @JSONField(label = "base")
    private boolean useSupport;
    @JSONField(label = "base")
    private boolean useOppose;

    public SoftwareDetailModel(SoftwareInfoModel model){
        super(model.getId(),model.getGroupId(),model.getName(),model.getLogo(),model.getDescription(),model.getDraft(),model.isPublished(),model.isDisabled(),model.isDeleted(),model.getCustomDescription(),model.getDraft(),model.getCommentCount(),model.getViewCount(),model.getSupportCount(),model.getOpposeCount(),model.getCreatedTime(),model.getCreatedUserId(),model.getCreatedIp(),model.getUpdatedTime(),model.getUpdatedUserId(),model.getUpdatedIp());
    }

    @Override
    public Long cacheKey() {
        return this.getId();
    }
}
