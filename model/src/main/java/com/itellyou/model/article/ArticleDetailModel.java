package com.itellyou.model.article;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.sys.CacheEntity;
import com.itellyou.model.collab.CollabInfoModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.util.annotation.JSONDefault;
import com.itellyou.util.serialize.EnumSerializer;
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
    private String title = "";
    @JSONField(label = "draft,base")
    private String content = "";
    @JSONField(label = "draft,base")
    private String html = "";
    @JSONField(label = "draft,base")
    private String description = "";
    @JSONField(label = "base")
    private boolean useStar;
    @JSONField(label = "draft,base", serializeUsing = EnumSerializer.class , deserializeUsing = EnumSerializer.class)
    private ArticleSourceType sourceType=ArticleSourceType.ORIGINAL;
    @JSONField(label = "draft,base")
    private String sourceData="";
    @JSONField(label = "draft,base")
    private List<TagDetailModel> tags = new ArrayList<>();
    @JSONField(label = "draft,base")
    private UserInfoModel author;
    @JSONField(label = "collab")
    private CollabInfoModel collab;
    @JSONField(label = "base")
    private ColumnInfoModel column;
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

    @Override
    public String cacheKey() {
        return String.valueOf(this.getId());
    }
}
