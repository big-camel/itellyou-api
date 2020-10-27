package com.itellyou.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataUpdateStepModel {
    private Long id;
    private Integer viewStep = 0;
    private Integer commentStep = 0;
    private Integer supportStep = 0;
    private Integer opposeStep = 0;
    private Integer starStep = 0;
}
