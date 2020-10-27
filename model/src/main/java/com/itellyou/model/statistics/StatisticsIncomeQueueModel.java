package com.itellyou.model.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsIncomeQueueModel<T extends StatisticsIncomeStepModel>  {
    private Long userId;
    private Long date;
    private T data;
}
