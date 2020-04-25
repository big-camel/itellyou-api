package com.itellyou.model.sys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysPathModel {

    private String path;
    private SysPath type;
    private Long id;
}
