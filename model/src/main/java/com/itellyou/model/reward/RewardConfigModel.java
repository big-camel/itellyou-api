package com.itellyou.model.reward;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardConfigModel {
    private String id;
    private RewardType type;
    private Double min;
    private Double max;
    private String unit;
}
