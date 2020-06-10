package com.itellyou.model.article;

import com.alibaba.fastjson.annotation.JSONField;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JSONDefault(includes = "base")
public class ArticleStarDetailModel extends ArticleStarModel {
    @JSONField(label = "base")
    private ArticleDetailModel article;
    @JSONField(label = "base")
    private UserDetailModel user;
}
