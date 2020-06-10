package com.itellyou.model.upload;

import com.itellyou.util.CacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadConfigModel implements CacheEntity {
    private String type;
    private String bucket;
    private String domain;
    private String endpoint;
    private Long size;

    @Override
    public String cacheKey() {
        return type;
    }
}
