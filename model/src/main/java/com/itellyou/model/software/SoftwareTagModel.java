package com.itellyou.model.software;

import com.itellyou.util.CacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoftwareTagModel implements CacheEntity {

    private Long softwareId;

    private Long tagId;

    @Override
    public String cacheKey() {
        return new StringBuilder(softwareId.toString()).append("-").append(tagId).toString();
    }
}
