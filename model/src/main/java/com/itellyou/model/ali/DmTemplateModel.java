package com.itellyou.model.ali;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DmTemplateModel implements Serializable {
    private String id;
    private String name;
    private String param;
    private String sendAddr;
    private String sendName;
    private String title;
    private String body;
    private String tagName;
    private Integer expire;
}
