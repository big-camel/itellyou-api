package com.itellyou.model.user;

import com.itellyou.model.common.IndexModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.util.annotation.JSONDefault;
import lombok.*;
import org.apache.lucene.document.Document;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JSONDefault(includes = "base")
public class UserIndexModel extends IndexModel {
    private String name;
    private String description;

    public UserIndexModel(Document document){
        super(document);
        this.setType(EntityType.USER);
        this.setName(document.get("name"));
        this.setTitle(this.getName());
        this.setDescription(document.get("description"));
        this.setContent(this.getDescription());
        this.setTitleField("name");
        this.setContentField("description");
    }
}
