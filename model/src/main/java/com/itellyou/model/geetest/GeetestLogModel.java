package com.itellyou.model.geetest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeetestLogModel implements Serializable {
    private String key;
    private GeetestClientTypeEnum type;
    private Long ip;
    private Integer status;
    private String mode;
    private Long createdUserId;
    private Long createdTime;
}
