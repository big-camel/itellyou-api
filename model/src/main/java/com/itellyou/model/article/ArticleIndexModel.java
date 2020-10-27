package com.itellyou.model.article;

import com.itellyou.model.common.IndexModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.util.StringUtils;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;
import org.apache.lucene.document.Document;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JSONDefault(includes = "base")
public class ArticleIndexModel extends IndexModel {
    private Long columnId;
    private Long createdUserId;

    public ArticleIndexModel(Document document){
        super(document);
        this.setType(EntityType.ARTICLE);
        String userId = document.get("created_user_id");
        this.setCreatedUserId(StringUtils.isNotEmpty(userId) ? Long.parseLong(userId) : 0);
        String columnId = document.get("column_id");
        this.setColumnId(StringUtils.isNotEmpty(columnId) ? Long.parseLong(columnId) : 0);
    }
}
