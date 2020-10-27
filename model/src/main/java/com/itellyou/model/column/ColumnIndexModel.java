package com.itellyou.model.column;

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
public class ColumnIndexModel extends IndexModel {
    private String name;
    private String description;
    private Long createdUserId;

    public ColumnIndexModel(Document document){
        super(document);
        this.setType(EntityType.COLUMN);
        String userId = document.get("created_user_id");
        this.setCreatedUserId(StringUtils.isNotEmpty(userId) ? Long.parseLong(userId) : 0);
        this.setName(document.get("name"));
        this.setDescription(document.get("description"));
        this.setTitle(this.getName());
        this.setContent(this.getDescription());
        this.setTitleField("name");
        this.setContentField("description");
    }
}
