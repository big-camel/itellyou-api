package com.itellyou.model.article;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.CacheEntity;
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
public class ArticleCommentDetailModel extends ArticleCommentModel implements CacheEntity {
    @JSONField(label = "base")
    private ArticleCommentDetailModel reply;
    @JSONField(label = "base")
    private List<ArticleCommentDetailModel> child;
    @JSONField(label = "article")
    private ArticleDetailModel article;
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

    public ArticleCommentDetailModel(ArticleCommentModel model){
        super(model.getId(),model.getArticleId(),model.getParentId(),model.getReplyId(),model.isDeleted(),model.getContent(),model.getHtml(),model.getCommentCount(),model.getSupport(),model.getOppose(),model.getCreatedTime(),model.getCreatedUserId(),model.getCreatedIp(),model.getUpdatedTime(),model.getUpdatedUserId(),model.getUpdatedIp());
    }

    @Override
    public String cacheKey() {
        return this.getId().toString();
    }
}
