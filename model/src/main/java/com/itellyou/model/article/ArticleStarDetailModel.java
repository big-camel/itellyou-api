package com.itellyou.model.article;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JSONDefault(includes = "base")
public class ArticleStarDetailModel extends ArticleStarModel {
    @JSONField(label = "base")
    private ArticleDetailModel article;
    @JSONField(label = "base")
    private UserInfoModel user;
}
