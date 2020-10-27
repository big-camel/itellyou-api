package com.itellyou.model.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsIncomeStepModel {
    private Long userId;
    private Double totalStep = 0.00;
    private Double tipStep = 0.00;
    private Double rewardStep = 0.00;
    private Double sharingStep = 0.00;
    private Double sellStep = 0.00;
    private Double otherStep = 0.00;
}
