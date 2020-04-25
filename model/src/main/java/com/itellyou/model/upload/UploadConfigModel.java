package com.itellyou.model.upload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadConfigModel {
    private String type;
    private String bucket;
    private String domain;
    private String endpoint;
    private Long size;
}
