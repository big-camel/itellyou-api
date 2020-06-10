package com.itellyou.model.column;

import com.itellyou.util.CacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnTagModel implements CacheEntity {

    private Long columnId;

    private Long tagId;

    @Override
    public String cacheKey() {
        return new StringBuilder(columnId.toString()).append("-").append(tagId).toString();
    }
}
