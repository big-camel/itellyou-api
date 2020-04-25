package com.itellyou.model.column;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class ColumnDetailModel extends ColumnInfoModel {
    @JSONField(label = "base")
    private String path="";
    @JSONField(label = "base")
    private List<TagInfoModel> tags;
    @JSONField(label = "base")
    private boolean useStar;
    @JSONField(label = "base")
    private UserDetailModel author;
}
