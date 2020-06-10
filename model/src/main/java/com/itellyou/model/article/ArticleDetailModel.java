package com.itellyou.model.article;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.collab.CollabInfoModel;
import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.util.CacheEntity;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserInfoModel;
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
public class ArticleDetailModel extends ArticleInfoModel implements CacheEntity {
    @JSONField(label = "draft,base")
    private String content = "";
    @JSONField(label = "draft,base")
    private String html = "";
    @JSONField(label = "base")
    private boolean useStar;
    @JSONField(label = "draft,base")
    private List<TagDetailModel> tags = new ArrayList<>();
    @JSONField(label = "draft,base")
    private UserInfoModel author;
    @JSONField(label = "collab")
    private CollabInfoModel collab;
    @JSONField(label = "base")
    private ColumnDetailModel column;
    @JSONField(label = "base")
    private boolean useSupport;
    @JSONField(label = "base")
    private boolean useOppose;
    @JSONField(label = "base")
    private boolean allowEdit;
    @JSONField(label = "base")
    private boolean allowSupport;
    @JSONField(label = "base")
    private boolean allowOppose;
    @JSONField(label = "base")
    private boolean allowStar;
    @JSONField(label = "draft,base")
    private ArticlePaidReadModel paidRead;

    public ArticleDetailModel(ArticleInfoModel model){
        super(model.getId(),model.getColumnId(),model.getSourceType(),model.getSourceData(),model.getTitle(),model.getDescription(),model.getDraft(),model.isPublished(),model.isDisabled(),model.isDeleted(),model.getCustomDescription(),model.getCover(),model.getDraft(),model.getCommentCount(),model.getView(),model.getSupport(),model.getOppose(),model.getStarCount(),model.getCreatedTime(),model.getCreatedUserId(),model.getCreatedIp(),model.getUpdatedTime(),model.getUpdatedUserId(),model.getUpdatedIp());
    }

    @Override
    public String cacheKey() {
        return String.valueOf(this.getId());
    }
}
