package com.itellyou.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBankModel {
    private Long userId;
    private Integer credit;
    private Double cash;
    private Integer score;
}
