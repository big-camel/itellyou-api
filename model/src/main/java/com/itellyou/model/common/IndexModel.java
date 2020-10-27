package com.itellyou.model.common;

import com.itellyou.model.sys.EntityType;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.lucene.document.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexModel implements CacheEntity<String> {
    private Long id;
    private EntityType type;
    private String title;
    private String content;
    private String titleField = "title";
    private String contentField = "content";

    public IndexModel(Document document){
        String id = document.get("id");
        this.setId(StringUtils.isNotEmpty(id) ? Long.parseLong(id) : null);
        this.setTitle(document.get("title"));
        this.setContent(document.get("content"));
    }

    @Override
    public String cacheKey() {
        return id + "-" + type.getValue();
    }
}
