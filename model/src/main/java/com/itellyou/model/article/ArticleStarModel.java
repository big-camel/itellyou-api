package com.itellyou.model.article;

import com.itellyou.model.common.StarModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;


@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JSONDefault(includes = "base")
public class ArticleStarModel extends StarModel {
    private Long articleId;

    public ArticleStarModel(Long id, Long createdTime,Long userId,Long ip){
        super();
        this.articleId = id;
        this.setCreatedUserId(userId);
        this.setCreatedTime(createdTime);
        this.setCreatedIp(ip);
    }
}
