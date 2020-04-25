package com.itellyou.model.tag;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class TagStarDetailModel extends TagStarModel {
    @JSONField(label = "base")
    private TagInfoModel tag;
    @JSONField(label = "base")
    private UserInfoModel user;
}
