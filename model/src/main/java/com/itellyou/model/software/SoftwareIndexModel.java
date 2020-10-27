package com.itellyou.model.software;

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
public class SoftwareIndexModel extends IndexModel {
    private Long groupId;
    private String name;
    private Long createdUserId;

    public SoftwareIndexModel(Document document){
        super(document);
        this.setType(EntityType.SOFTWARE);
        String userId = document.get("created_user_id");
        this.setCreatedUserId(StringUtils.isNotEmpty(userId) ? Long.parseLong(userId) : 0);
        String groupId = document.get("group_id");
        this.setGroupId(StringUtils.isNotEmpty(groupId) ? Long.parseLong(groupId) : 0);
        this.setName(document.get("name"));
        this.setTitle(this.getName());
        this.setTitleField("name");
    }
}
