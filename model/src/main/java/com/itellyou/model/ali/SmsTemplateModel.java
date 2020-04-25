package com.itellyou.model.ali;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsTemplateModel implements Serializable {
    private String id;
    private String name;
    private String code;
    private String param;
    private String signName;
    private Integer expire;
}
