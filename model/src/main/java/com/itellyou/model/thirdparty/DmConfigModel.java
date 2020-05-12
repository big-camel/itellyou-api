package com.itellyou.model.thirdparty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DmConfigModel implements Serializable {
    private String type;
    private Integer minute=0;
    private Integer hour=0;
    private Integer day=0;
}
