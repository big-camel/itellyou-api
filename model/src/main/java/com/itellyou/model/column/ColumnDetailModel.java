package com.itellyou.model.column;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.util.CacheEntity;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class ColumnDetailModel extends ColumnInfoModel implements CacheEntity {
    @JSONField(label = "base")
    private String path="";
    @JSONField(label = "base")
    private List<TagDetailModel> tags;
    @JSONField(label = "base")
    private boolean useStar;
    @JSONField(label = "base")
    private UserDetailModel author;

    public ColumnDetailModel(ColumnInfoModel model){
        super(model.getId(),model.getName(),model.getAvatar(),model.getDescription(),model.isDisabled(),model.isDeleted(),model.isReviewed(),model.getMemberCount(),model.getArticleCount(),model.getStarCount(),model.getCreatedTime(),model.getCreatedUserId(),model.getCreatedIp(),model.getUpdatedUserId(),model.getUpdatedTime(),model.getUpdatedIp());
    }

    @Override
    public String cacheKey() {
        return String.valueOf(this.getId());
    }
}
