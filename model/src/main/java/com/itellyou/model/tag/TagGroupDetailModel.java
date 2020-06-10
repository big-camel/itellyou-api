package com.itellyou.model.tag;

import com.alibaba.fastjson.annotation.JSONField;
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
public class TagGroupDetailModel extends TagGroupModel {
    @JSONField(label = "base")
    private List<TagDetailModel> tagList;
    @JSONField(label = "base")
    private UserDetailModel author;

    public TagGroupDetailModel(TagGroupModel model){
        super(model.getId(),model.getName(),model.getTagCount(),model.getCreatedTime(),model.getCreatedUserId(),model.getCreatedIp());
    }
}
