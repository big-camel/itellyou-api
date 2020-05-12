package com.itellyou.model.article;

import com.itellyou.model.common.IndexModel;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JSONDefault(includes = "base")
public class ArticleIndexModel extends IndexModel {
    private Long columnId;
    private String title;
    private String content;
    private Long createdUserId;
}
