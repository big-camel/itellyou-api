package com.itellyou.model.thirdparty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsLogModel implements Serializable {
    private Long id;
    private String mobile;
    private String templateId;
    private String data;
    private Integer status;
    private LocalDateTime createdTime;
    private Long createdIp;
}
